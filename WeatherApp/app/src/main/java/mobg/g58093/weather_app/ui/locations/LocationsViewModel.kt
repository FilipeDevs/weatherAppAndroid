package mobg.g58093.weather_app.ui.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobg.g58093.weather_app.data.WeatherEntry
import mobg.g58093.weather_app.data.WeatherRepository

data class Locations(
    val locationsList : List<WeatherEntry> = listOf()
)

class LocationsViewModel : ViewModel() {

    private val _locationsState = MutableStateFlow<Locations>(Locations())
    val locationsState: StateFlow<Locations> = _locationsState.asStateFlow()

    fun getAllUserLocations() {
        viewModelScope.launch {
            val weatherList = WeatherRepository.getAllWeatherEntries()
            _locationsState.update { currentState -> currentState.copy(locationsList =  weatherList) }
        }
    }

}