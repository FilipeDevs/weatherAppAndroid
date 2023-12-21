package mobg.g58093.weather_app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mobg.g58093.weather_app.network.RetroApi
import mobg.g58093.weather_app.network.WeatherResponse

sealed class WeatherApiState {
    object Loading : WeatherApiState()
    data class Success(val data: WeatherResponse) : WeatherApiState()
    data class Error(val message: String) : WeatherApiState()
}

class HomeViewModel : ViewModel() {

    private val _weatherState = MutableStateFlow<WeatherApiState>(WeatherApiState.Loading)
    val weatherState: StateFlow<WeatherApiState> = _weatherState

    init {
        getWeatherByCoordinates(38.7077507, -9.1365919, "metric",
            "58701429b6088e321356701fde1e7ed0")
    }


    fun getWeatherByCoordinates(latitude: Double, longitude: Double, units : String, apiKey: String) {
        viewModelScope.launch {
            try {
                _weatherState.value = WeatherApiState.Loading
                val response = RetroApi.weatherService.getWeatherByCoordinates(latitude, longitude, units, apiKey)
                _weatherState.value = WeatherApiState.Success(response)
            } catch (e: Exception) {
                _weatherState.value = WeatherApiState.Error("Error fetching data: ${e.message}")
            }
        }
    }
}

