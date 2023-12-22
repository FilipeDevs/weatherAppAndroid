package mobg.g58093.weather_app.ui.home

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mobg.g58093.weather_app.data.WeatherEntry
import mobg.g58093.weather_app.data.WeatherRepository
import mobg.g58093.weather_app.network.RetroApi
import mobg.g58093.weather_app.network.WeatherResponse
import mobg.g58093.weather_app.ui.LocationState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class WeatherApiState {
    object Loading : WeatherApiState()
    data class Success(val data: WeatherEntry) : WeatherApiState()
    data class Error(val message: String) : WeatherApiState()
}

class HomeViewModel : ViewModel() {

    private val _weatherState = MutableStateFlow<WeatherApiState>(WeatherApiState.Loading)
    val weatherState: StateFlow<WeatherApiState> = _weatherState

    private val _locationState = MutableStateFlow(LocationState())
    val locationState : StateFlow<LocationState> = _locationState.asStateFlow()

    init {
        var weatherEntry : WeatherEntry?
        var isCurrentLocation = false
        viewModelScope.launch {
            if (locationState.value.selectedLocation == "currentLocation") {
                weatherEntry = WeatherRepository.getWeatherEntryCurrentLocation()
                isCurrentLocation = true
            } else {
                weatherEntry = WeatherRepository.getWeatherEntry(locationState.value.selectedLocation)
            }
            weatherEntry?.let {
                getWeatherByCoordinates(
                    it.latitude, it.longitude, "metric",
                    "58701429b6088e321356701fde1e7ed0", isCurrentLocation)
            }
        }
    }


    private fun getWeatherByCoordinates(latitude: Double, longitude: Double, units : String, apiKey: String, isCurrentLocation : Boolean) {
        viewModelScope.launch {
            try {
                _weatherState.value = WeatherApiState.Loading
                val response = RetroApi.weatherService.getWeatherByCoordinates(latitude, longitude, units, apiKey)
                val weatherEntry = WeatherEntry(
                    0,
                    response.name,
                    response.coord.lat,
                    response.coord.lon,
                    convertCurrentDateToFormattedDate(),
                    response.main.temp.toInt(),
                    response.main.temp_max.toInt(),
                    response.main.temp_min.toInt(),
                    response.weather[0].description,
                    response.weather[0].icon,
                    convertUnixTimestampToHourAndMinutes(response.sys.sunrise),
                    convertUnixTimestampToHourAndMinutes(response.sys.sunset),
                    response.wind.speed,
                    response.main.humidity,
                    response.visibility,
                    response.main.pressure,
                    isCurrentLocation)
                _weatherState.value = WeatherApiState.Success(response)
            } catch (e: Exception) {
                _weatherState.value = WeatherApiState.Error("Error fetching data: ${e.message}")
            }
        }
    }

    fun convertCurrentDateToFormattedDate(): String {
        val date = Date()
        val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    fun convertUnixTimestampToHourAndMinutes(unixTimestamp: Long): String {
        val date = Date(unixTimestamp * 1000L)
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(date)
    }
}

