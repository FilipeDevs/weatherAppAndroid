package mobg.g58093.weather_app.ui.forecast

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
    application: Application
) : AndroidViewModel(application) {

    private val context = application

    private val _forecastState = MutableStateFlow<ForecastApiState>(ForecastApiState.Loading)
    val forecastState: StateFlow<ForecastApiState> = _forecastState

    private val apiKey = PropertiesManager.getApiKey(application)
    private val units = PropertiesManager.getUnits(application)

    private val TAG = "ForecastViewModel"

    /**
     * Initializes the ForecastViewModel. Observes changes to the selected location state
     * and initiates the forecast data retrieval process.
     */
    init {
        viewModelScope.launch {
            getForecast()
        }
    }


    /**
     * Converts a list of forecast responses (40 timestamps) to a narrowed-down list containing 5 timestamps
     * (one for each corresponding day).
     */
    private fun convertResponseToFiveDays(forecastResponseList: List<ForecastWeather>)
            : MutableList<ForecastWeather> {
        val forecastList: MutableList<ForecastWeather> = mutableListOf()
        for (i in forecastResponseList.indices step 8) {
            forecastList.add(forecastResponseList[i])
        }

        return forecastList
    }

    /**
     * Initiates the process of fetching forecast data and
     * updates the forecast state accordingly.
     */
    private fun getForecast() {
        viewModelScope.launch {
            try {
                _forecastState.value = ForecastApiState.Loading // Loading
                if (isOnline(context)) { // Does the app have internet connection ?
                    // Weather api call to OpenWeather (forecast data)
                    val response = RetroApi.weatherService.getWeatherForecast(
                        SelectedLocationRepository.selectedLocationState.value.latitude,
                        SelectedLocationRepository.selectedLocationState.value.longitude,
                        units,
                        apiKey
                    )

                    val forecastList = convertResponseToFiveDays(response.list)

                    // Run db operations
                    withContext(Dispatchers.IO) {
                        val mainWeather: WeatherEntry = WeatherRepository.getWeatherEntry(
                            SelectedLocationRepository.selectedLocationState.value.latitude,
                            SelectedLocationRepository.selectedLocationState.value.longitude
                        )!!
                        val forecastEntries =
                            WeatherRepository.getAllForecastsByLocation(mainWeather.id)
                        if (forecastEntries.isNotEmpty()) { // forecast entries already exist
                            _forecastState.value = ForecastApiState.Success(
                                convertResponseToForecastEntryUpdate(forecastList, mainWeather)
                            )
                        } else { // insert fresh new data
                            _forecastState.value = ForecastApiState.Success(
                                convertResponseToForecastEntryInsert(forecastList, mainWeather)
                            )
                        }
                    }
                } else { // No internet connection
                    // Load data from the local database
                    withContext(Dispatchers.IO) {
                        val mainWeather: WeatherEntry = WeatherRepository.getWeatherEntry(
                            SelectedLocationRepository.selectedLocationState.value.latitude,
                            SelectedLocationRepository.selectedLocationState.value.longitude
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

    /**
     * Converts forecast response data to forecast entries and updates the existing entries in the database.
     */
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

    /**
     * Converts forecast response data to forecast entries and inserts them into the database.
     */
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

    /**
     * Observes changes to the selected location state and updates the weather data accordingly.
     */
    private fun convertUnixTimestampToDayAndMonth(unixTimestamp: Long): String {
        val date = Date(unixTimestamp * 1000L)
        val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
        return sdf.format(date)
    }
}

