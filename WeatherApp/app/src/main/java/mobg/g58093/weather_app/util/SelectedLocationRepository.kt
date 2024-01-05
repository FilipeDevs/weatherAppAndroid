package mobg.g58093.weather_app.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SelectedLocationState(
    val locationName: String = "",
    val countryCode: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val currentLocation: Boolean = true,
)

class SelectedLocationRepository {
    private val _selectedLocationState = MutableStateFlow(SelectedLocationState())
    val selectedLocationState: StateFlow<SelectedLocationState> =
        _selectedLocationState.asStateFlow()

    fun editSelectLocation(selectedLocationState: SelectedLocationState) {
        _selectedLocationState.value = selectedLocationState
    }

    fun resetSelectedLocation() {
        _selectedLocationState.value = SelectedLocationState()
    }
}

