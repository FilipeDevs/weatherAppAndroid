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
import mobg.g58093.weather_app.util.SelectedLocationRepository
import mobg.g58093.weather_app.util.SelectedLocationState
import mobg.g58093.weather_app.data.ForecastEntry
import mobg.g58093.weather_app.network.responses.ForecastWeather
import mobg.g58093.weather_app.data.WeatherEntry
import mobg.g58093.weather_app.data.WeatherRepository
import mobg.g58093.weather_app.network.RetroApi
import mobg.g58093.weather_app.network.isOnline
import mobg.g58093.weather_app.util.PropertiesManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class ForecastApiState {
    object Loading : ForecastApiState()
    data class Success(val data: List<ForecastEntry>) : ForecastApiState()
    data class Error(val message: String) : ForecastApiState()
}

class ForecastViewModel(
    application: Application,
    private val selectedLocationRepository: SelectedLocationRepository
) : AndroidViewModel(application) {

    private var selectedLocation = SelectedLocationState()

    private val context = application

    private val _forecastState = MutableStateFlow<ForecastApiState>(ForecastApiState.Loading)
    val forecastState: StateFlow<ForecastApiState> = _forecastState

    private val apiKey = PropertiesManager.getApiKey(application)
    private val units = PropertiesManager.getUnits(application)

    private val TAG = "ForecastViewModel"

    init {
        viewModelScope.launch {
            observeSelectedCityState()
            getForecast()
        }
    }

    // Narrow down the forecast to 5 timestamps (instead of 40) each for a corresponding day
    private fun convertResponseToFiveDays(forecastResponseList: List<ForecastWeather>)
            : MutableList<ForecastWeather> {
        val forecastList: MutableList<ForecastWeather> = mutableListOf()
        for (i in forecastResponseList.indices step 8) {
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
                    val response = RetroApi.weatherService.getWeatherForecast(
                        selectedLocation.latitude,
                        selectedLocation.longitude, units, apiKey
                    )

                    val forecastList = convertResponseToFiveDays(response.list)

                    // Run db operations
                    withContext(Dispatchers.IO) {
                        val mainWeather: WeatherEntry = WeatherRepository.getWeatherEntry(
                            selectedLocation.latitude, selectedLocation.longitude
                        )!!
                        val forecastEntries =
                            WeatherRepository.getAllForecastsByLocation(mainWeather.id)
                        if (forecastEntries.isNotEmpty()) { // forecast entries already exist
                            _forecastState.value = ForecastApiState.Success(
                                convertResponseToForecastEntryUpdate(forecastList, mainWeather)
                            )
                        } else {
                            _forecastState.value = ForecastApiState.Success(
                                convertResponseToForecastEntryInsert(forecastList, mainWeather)
                            )
                        }
                    }
                } else { // No internet connection
                    // Load data from the local database
                    withContext(Dispatchers.IO) {
                        val mainWeather: WeatherEntry = WeatherRepository.getWeatherEntry(
                            selectedLocation.latitude, selectedLocation.longitude
                        )!!
                        val forecastEntries =
                            WeatherRepository.getAllForecastsByLocation(mainWeather.id)
                        if (forecastEntries.isNotEmpty()) {
                            _forecastState.value = ForecastApiState.Success(forecastEntries)
                        } else {
                            _forecastState.value =
                                ForecastApiState.Error("No internet connection and no forecast data in the database.")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching data: ${e.message}")
                _forecastState.value = ForecastApiState.Error("Error fetching data: ${e.message}")
            }
        }
    }

    private suspend fun convertResponseToForecastEntryUpdate(
        forecastWeather: List<ForecastWeather>,
        weatherEntry: WeatherEntry
    ): List<ForecastEntry> {
        val existingForecastEntires = WeatherRepository.getAllForecastsByLocation(weatherEntry.id)

        for (index in forecastWeather.indices) {
            val forecast = existingForecastEntires[index]
            WeatherRepository.updateForecastEntry(
                forecast.copy(
                    temp = forecastWeather[index].main.temp.toInt(),
                    icon = forecastWeather[index].weather[0].icon,
                    humidity = forecastWeather[index].main.humidity,
                    date = convertUnixTimestampToDayAndMonth(forecastWeather[index].dt)
                )
            )
        }

        return existingForecastEntires

    }

    private suspend fun convertResponseToForecastEntryInsert(
        forecastWeather: List<ForecastWeather>,
        weatherEntry: WeatherEntry
    ): List<ForecastEntry> {
        for (forecast in forecastWeather) {
            WeatherRepository.insertForecastEntry(
                ForecastEntry(
                    id = 0,
                    temp = forecast.main.temp.toInt(),
                    icon = forecast.weather[0].icon,
                    humidity = forecast.main.humidity,
                    locationName = weatherEntry.locationName,
                    date = convertUnixTimestampToDayAndMonth(forecast.dt),
                    weatherEntryId = weatherEntry.id
                )
            )
        }

        return WeatherRepository.getAllForecastsByLocation(weatherEntry.id)
    }

    private fun convertUnixTimestampToDayAndMonth(unixTimestamp: Long): String {
        val date = Date(unixTimestamp * 1000L)
        val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
        return sdf.format(date)
    }

    private fun observeSelectedCityState() {
        viewModelScope.launch {
            selectedLocationRepository.selectedLocationState.collect { newState ->
                Log.d(TAG, "New selected location: $newState")
                selectedLocation = newState
            }
        }
    }
}

