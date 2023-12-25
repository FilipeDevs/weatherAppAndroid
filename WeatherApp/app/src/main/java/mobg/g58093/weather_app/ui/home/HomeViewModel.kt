package mobg.g58093.weather_app.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mobg.g58093.weather_app.data.WeatherEntry
import mobg.g58093.weather_app.data.WeatherRepository
import mobg.g58093.weather_app.getUserLocation
import mobg.g58093.weather_app.network.RetroApi
import mobg.g58093.weather_app.network.WeatherResponse
import mobg.g58093.weather_app.network.isOnline
import mobg.g58093.weather_app.ui.LocationState
import mobg.g58093.weather_app.ui.common.SelectedCityViewModel
import mobg.g58093.weather_app.ui.common.SelectedLocationState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


sealed class WeatherApiState {
    object Loading : WeatherApiState()
    data class Success(val data: WeatherEntry) : WeatherApiState()
    data class Error(val message: String) : WeatherApiState()
}

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application

    private val _weatherState = MutableStateFlow<WeatherApiState>(WeatherApiState.Loading)
    val weatherState: StateFlow<WeatherApiState> = _weatherState

    private val _locationState = MutableStateFlow(LocationState())
    val locationState : StateFlow<LocationState> = _locationState.asStateFlow()

    private val TAG = "HomeViewModel"

    init {
        viewModelScope.launch {
            getUserLocation(context) { locationState ->
                if (locationState.isLocationPermissionGranted) {
                    getWeatherByCoordinates(
                        locationState.latitude, locationState.longitude, "metric",
                        "58701429b6088e321356701fde1e7ed0", false
                    )
                } else {
                    // Handle the case where location permission is not granted
                    _weatherState.value = WeatherApiState.Error("Location permission not granted.")
                }
            }
        }
    }

    private fun getWeatherByCoordinates(latitude: Double, longitude: Double, units: String, apiKey: String, isCurrentLocation: Boolean) {
        viewModelScope.launch {
            try {
                _weatherState.value = WeatherApiState.Loading

                if (isOnline(context)) {
                    val response = RetroApi.weatherService.getWeatherByCoordinates(latitude, longitude, units, apiKey)
                    _locationState.update { currentState -> currentState.copy(selectedLocation = response.name) }
                    withContext(Dispatchers.IO) {
                        val existingWeatherEntryId = WeatherRepository.getWeatherEntry(response.name)?.id ?: 0
                        val weatherEntry = convertWeatherResponseToWeatherEntry(response, existingWeatherEntryId, isCurrentLocation)
                        if (existingWeatherEntryId != 0) {
                            WeatherRepository.updateWeatherEntry(weatherEntry)
                        } else {
                            WeatherRepository.insertWeatherEntry(weatherEntry)
                        }
                        _weatherState.value = WeatherApiState.Success(weatherEntry)
                    }


                } else {
                    // Load data from the local database
                    withContext(Dispatchers.IO) {
                        val localWeatherEntry = WeatherRepository.getWeatherEntry(locationState.value.selectedLocation)
                        if (localWeatherEntry != null) {
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

    fun convertCurrentDateToFormattedDate(): String {
        val date = Date()
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(date)
    }

    fun convertUnixTimestampToHourAndMinutes(unixTimestamp: Long): String {
        val date = Date(unixTimestamp * 1000L)
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(date)
    }

    fun convertWeatherResponseToWeatherEntry(weatherResponse: WeatherResponse, id : Int, isCurrentLocation: Boolean) : WeatherEntry {
        return WeatherEntry(
            id = id,
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
            currentLocation = isCurrentLocation,
        )
    }

    fun refreshData() {
        viewModelScope.launch {
            getWeatherByCoordinates(
                locationState.value.latitude,  locationState.value.longitude, "metric",
                    "58701429b6088e321356701fde1e7ed0", false)
        }
    }

}

