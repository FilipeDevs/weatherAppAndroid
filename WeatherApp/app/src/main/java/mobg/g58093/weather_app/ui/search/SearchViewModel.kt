package mobg.g58093.weather_app.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mobg.g58093.weather_app.util.PropertiesManager
import mobg.g58093.weather_app.data.WeatherEntry
import mobg.g58093.weather_app.data.WeatherRepository
import mobg.g58093.weather_app.network.responses.LocationWeatherResponse
import mobg.g58093.weather_app.network.RetroApi
import mobg.g58093.weather_app.network.responses.WeatherResponse
import mobg.g58093.weather_app.network.isOnline
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


sealed class SearchApiState {
    object Loading : SearchApiState()
    data class Success(val data: List<LocationWeatherResponse> = listOf()) : SearchApiState()
    data class Error(val message: String) : SearchApiState()
}

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application

    // Main state
    private val _searchState = MutableStateFlow<SearchApiState>(SearchApiState.Success())
    val searchState: StateFlow<SearchApiState> = _searchState

    private val apiKey = PropertiesManager.getApiKey(application)
    private val units = PropertiesManager.getUnits(application)


    /**
     * Initiates a search for locations based on the provided [searchQuery].
     */
    fun searchLocation(searchQuery : String) {
        viewModelScope.launch {
            _searchState.value = SearchApiState.Loading
            if(isOnline(context)) { // Check internet connection
                try {
                    val response = RetroApi.weatherService.getCityWeather(searchQuery, 5,apiKey)
                    _searchState.value = SearchApiState.Success(response)
                } catch (e: Exception) {
                    _searchState.value = SearchApiState.Error("An unexpected error occurred")
                }
            } else {
                _searchState.value = SearchApiState.Error("Unable to search locations due to lack " +
                        "of internet connection")
            }
        }
    }

    /**
     * Adds the provided [location] to the favorites locations if it has not been added already.
     */
    fun addLocationToFavorites(location: LocationWeatherResponse) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val weatherEntry : WeatherEntry? = WeatherRepository.getWeatherEntry(location.lat, location.lon)
                if(weatherEntry == null) { // The location has not been added yet
                    // Weather api call to OpenWeather
                    val response = RetroApi.weatherService.getWeatherByCoordinates(location.lat, location.lon, units, apiKey)
                    // Run db operations
                    val newWeatherEntry = convertWeatherResponseToWeatherEntry(response)
                    WeatherRepository.insertWeatherEntry(newWeatherEntry)

                }
            }
        }
    }

    /**
     * Converts a weather response to a [WeatherEntry] object.
     */
    private fun convertWeatherResponseToWeatherEntry(weatherResponse: WeatherResponse) : WeatherEntry {
        return WeatherEntry(
            id = 0,
            locationName = weatherResponse.name,
            longitude = weatherResponse.coord.lon,
            latitude = weatherResponse.coord.lat,
            date = convertCurrentDateToFormattedDate(),
            mainTemp = weatherResponse.main.temp.toInt(),
            highTemp = weatherResponse.main.temp_max.toInt(),
            lowTemp = weatherResponse.main.temp_min.toInt(),
            weatherType = weatherResponse.weather[0].description,
            weatherIcon =  weatherResponse.weather[0].icon,
            sunriseHour =  convertUnixTimestampToHourAndMinutes(weatherResponse.sys.sunrise),
            sunsetHour =  convertUnixTimestampToHourAndMinutes(weatherResponse.sys.sunset),
            wind = weatherResponse.wind.speed,
            humidity =  weatherResponse.main.humidity,
            visibility = weatherResponse.visibility,
            pressure = weatherResponse.main.pressure,
            currentLocation = false,
            country = weatherResponse.sys.country
        )
    }

    /**
     * Converts a Unix timestamp to a formatted string representing the hour and minutes.
     */
    private fun convertCurrentDateToFormattedDate(): String {
        val date = Date()
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(date)
    }

    /**
     * Converts the current date to a formatted string.
     */
    private fun convertUnixTimestampToHourAndMinutes(unixTimestamp: Long): String {
        val date = Date(unixTimestamp * 1000L)
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(date)
    }

}