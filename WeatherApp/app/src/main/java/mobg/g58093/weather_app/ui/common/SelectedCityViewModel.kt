package mobg.g58093.weather_app.ui.common

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import mobg.g58093.weather_app.ui.home.WeatherApiState

data class SelectedLocationState(
    var latitude: Double = 0.0,
    val longitude: Double = 0.0,
)

class SelectedCityViewModel(application: Application) : AndroidViewModel(application) {
    private val _selectedLocation = MutableStateFlow<SelectedLocationState>(SelectedLocationState())
    val selectedLocation: StateFlow<SelectedLocationState> = _selectedLocation

    fun setSelectedLocation(latitude: Double, longitude: Double) {
        _selectedLocation.update { currentState -> currentState.copy(longitude = longitude, latitude = latitude) }
    }

    fun fetchSelectedLocation(): StateFlow<SelectedLocationState> {
        return selectedLocation
    }
}
