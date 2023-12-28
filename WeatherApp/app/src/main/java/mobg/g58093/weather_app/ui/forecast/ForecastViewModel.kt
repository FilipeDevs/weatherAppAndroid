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
import mobg.g58093.weather_app.data.ForecastWeatherEntry
import mobg.g58093.weather_app.data.WeatherEntry
import mobg.g58093.weather_app.data.WeatherRepository
import mobg.g58093.weather_app.network.RetroApi
import mobg.g58093.weather_app.network.isOnline
import mobg.g58093.weather_app.ui.home.WeatherApiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class ForecastApiState {
    object Loading : ForecastApiState()
    data class Success(val data: WeatherEntry) : ForecastApiState()
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
        }
    }

    private fun getForecast() {
        viewModelScope.launch {
            try {
                _forecastState.value = ForecastApiState.Loading // Loading
                if (isOnline(context)) { // Does the app have internet connection ?
                    // Weather api call to OpenWeather (forecast data)
                    val response = RetroApi.weatherService.getWeatherForecast(selectedLocation.latitude,
                        selectedLocation.longitude, "metric", 7, "58701429b6088e321356701fde1e7ed0")
                    // Run db operations
                    withContext(Dispatchers.IO) {
                        // Convert response to DTO
                        if(WeatherRepository.getAllForecastsByLocation(response.city.name).isNotEmpty()) { // forecast entries already exist
                            convertResponseToForecastEntryUpdate(response.list, response.city.name)
                        } else {
                            convertResponseToForecastEntryInsert(response.list, response.city.name)
                        }


                        if (existingWeatherEntryId != 0) {
                            WeatherRepository.updateWeatherEntry(weatherEntry)
                        } else {
                            WeatherRepository.insertWeatherEntry(weatherEntry)
                        }
                        selectedLocationRepository.editSelectLocation(SelectedLocationState(
                            weatherEntry.locationName, weatherEntry.longitude, weatherEntry.latitude, isCurrentLocation))
                        _weatherState.value = WeatherApiState.Success(weatherEntry)
                    }
                } else { // No internet connection
                    // Load data from the local database
                    withContext(Dispatchers.IO) {
                        val localWeatherEntry = WeatherRepository.getWeatherEntry(selectedLocation.locationName)
                        if (localWeatherEntry != null) {
                            selectedLocationRepository.editSelectLocation(SelectedLocationState(
                                localWeatherEntry.locationName, localWeatherEntry.longitude, localWeatherEntry.latitude, isCurrentLocation))
                            _weatherState.value = WeatherApiState.Success(localWeatherEntry)
                        } else {
                            _weatherState.value = WeatherApiState.Error("No internet connection and no data in the database.")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching data: ${e.message}")
                _weatherState.value = WeatherApiState.Error("Error fetching data: ${e.message}")
            }
        }
    }

    suspend fun convertResponseToForecastEntryUpdate(forecastWeather: List<ForecastWeather>, locationName : String) {
        val existingForecastEntires = WeatherRepository.getAllForecastsByLocation(locationName)

        for(index in forecastWeather.indices) {
            val forecast = existingForecastEntires[index]
            WeatherRepository.updateForecastEntry(forecast.copy(
                tempMax = forecastWeather[index].temp.max.toInt(),
                tempMin = forecastWeather[index].temp.min.toInt(),
                icon = forecastWeather[index].weather.icon,
                humidity = forecastWeather[index].humidity,
                date = convertUnixTimestampToHourAndMinutes(forecastWeather[index].dt)
            ))
        }

    }

    suspend fun convertResponseToForecastEntryInsert(forecastWeather: List<ForecastWeather>, locationName : String) {
        for(forecast in forecastWeather) {
            WeatherRepository.insertForecastEntry(
                ForecastEntry(
                    id = 0,
                    tempMax = forecast.temp.max.toInt(),
                    tempMin = forecast.temp.min.toInt(),
                    icon = forecast.weather.icon,
                    humidity = forecast.humidity,
                    locationName = locationName,
                    date = convertUnixTimestampToHourAndMinutes(forecast.dt)
                )
            )
        }
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

