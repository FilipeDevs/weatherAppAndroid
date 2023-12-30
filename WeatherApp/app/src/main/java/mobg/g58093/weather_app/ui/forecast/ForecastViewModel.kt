package mobg.g58093.weather_app.ui.forecast

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mobg.g58093.weather_app.SelectedLocationRepository
import mobg.g58093.weather_app.SelectedLocationState
import mobg.g58093.weather_app.data.ForecastEntry
import mobg.g58093.weather_app.data.ForecastResponse
import mobg.g58093.weather_app.data.ForecastWeather
import mobg.g58093.weather_app.data.MainWeather
import mobg.g58093.weather_app.data.WeatherEntry
import mobg.g58093.weather_app.data.WeatherRepository
import mobg.g58093.weather_app.network.RetroApi
import mobg.g58093.weather_app.network.isOnline
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class ForecastApiState {
    object Loading : ForecastApiState()
    data class Success(val data: List<ForecastEntry>) : ForecastApiState()
    data class Error(val message: String) : ForecastApiState()
}

class ForecastViewModel(application: Application, private val selectedLocationRepository: SelectedLocationRepository) : AndroidViewModel(application) {

    private var selectedLocation = SelectedLocationState()

    private val context = application

    private val _forecastState = MutableStateFlow<ForecastApiState>(ForecastApiState.Loading)
    val forecastState: StateFlow<ForecastApiState> = _forecastState

    private val TAG = "ForecastViewModel"

    init {
        viewModelScope.launch {
            observeSelectedCityState()
            getForecast()
        }
    }

    private fun convertResponseToFiveDays(forecastResponseList: List<ForecastWeather>)
    : MutableList<ForecastWeather>  {
        val forecastList : MutableList<ForecastWeather> = mutableListOf()
        for(i in forecastResponseList.indices step 8) {
            forecastList.add(forecastResponseList[i])
        }

        return forecastList
    }

    private fun getForecast() {
        viewModelScope.launch {
            try {
                _forecastState.value = ForecastApiState.Loading // Loading
                if (isOnline(context)) { // Does the app have internet connection ?
                    // Weather api call to OpenWeather (forecast data)
                    val response = RetroApi.weatherService.getWeatherForecast(selectedLocation.latitude,
                        selectedLocation.longitude, "metric", "58701429b6088e321356701fde1e7ed0")
                    // Run db operations

                    val forecastList = convertResponseToFiveDays(response.list)

                    withContext(Dispatchers.IO) {
                        val mainWeather : WeatherEntry = WeatherRepository.getWeatherEntry(selectedLocation.locationName)!!
                        val forecastEntries = WeatherRepository.getAllForecastsByLocation(selectedLocation.locationName)
                        if(forecastEntries.isNotEmpty()) { // forecast entries already exist
                            _forecastState.value = ForecastApiState.Success(
                                convertResponseToForecastEntryUpdate(forecastList, mainWeather))
                        } else {
                            _forecastState.value = ForecastApiState.Success(
                                convertResponseToForecastEntryInsert(forecastList, mainWeather))
                        }
                    }
                } else { // No internet connection
                    // Load data from the local database
                    withContext(Dispatchers.IO) {
                        val forecastEntries = WeatherRepository.getAllForecastsByLocation(selectedLocation.locationName)
                        if (forecastEntries.isNotEmpty()) {
                            _forecastState.value = ForecastApiState.Success(forecastEntries)
                        } else {
                            _forecastState.value = ForecastApiState.Error("No internet connection and no forecast data in the database.")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching data: ${e.message}")
                _forecastState.value = ForecastApiState.Error("Error fetching data: ${e.message}")
            }
        }
    }

    suspend fun convertResponseToForecastEntryUpdate(forecastWeather: List<ForecastWeather>, weatherEntry : WeatherEntry) : List<ForecastEntry> {
        val existingForecastEntires = WeatherRepository.getAllForecastsByLocation(weatherEntry.locationName)

        for(index in forecastWeather.indices) {
            val forecast = existingForecastEntires[index]
            WeatherRepository.updateForecastEntry(forecast.copy(
                tempMax = forecastWeather[index].main.temp_max.toInt(),
                tempMin = forecastWeather[index].main.temp_min.toInt(),
                icon = forecastWeather[index].weather[0].icon,
                humidity = forecastWeather[index].main.humidity,
                date = convertUnixTimestampToHourAndMinutes(forecastWeather[index].dt)
            ))
        }

        return existingForecastEntires

    }

    suspend fun convertResponseToForecastEntryInsert(forecastWeather: List<ForecastWeather>, weatherEntry : WeatherEntry) : List<ForecastEntry> {
        for(forecast in forecastWeather) {
            WeatherRepository.insertForecastEntry(
                ForecastEntry(
                    id = 0,
                    tempMax = forecast.main.temp_max.toInt(),
                    tempMin = forecast.main.temp_min.toInt(),
                    icon = forecast.weather[0].icon,
                    humidity = forecast.main.humidity,
                    locationName = weatherEntry.locationName,
                    date = convertUnixTimestampToHourAndMinutes(forecast.dt),
                    weatherEntryId = weatherEntry.id
                )
            )
        }

        return WeatherRepository.getAllForecastsByLocation(weatherEntry.locationName)
    }

    fun convertUnixTimestampToHourAndMinutes(unixTimestamp: Long): String {
        val date = Date(unixTimestamp * 1000L)
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(date)
    }

    private fun observeSelectedCityState() {
        viewModelScope.launch {
            selectedLocationRepository.selectedLocationState.collect { newState ->
                Log.d(TAG, "New selected location: $newState")
                selectedLocation =  newState
            }
        }
    }



}

