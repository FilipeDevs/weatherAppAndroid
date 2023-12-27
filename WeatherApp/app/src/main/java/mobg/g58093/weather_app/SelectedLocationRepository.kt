package mobg.g58093.weather_app


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SelectedLocationState(
    val locationName : String = "currentLocation",
    val longitude : Double = 0.0,
    val latitude : Double = 0.0,
    val currentLocation : Boolean = true,
)


class SelectedLocationRepository {
    // Shared data source
    private val _selectedLocationState = MutableStateFlow(SelectedLocationState())
    val selectedLocationState : StateFlow<SelectedLocationState> = _selectedLocationState.asStateFlow()


    fun editSelectLocation(selectedLocationState: SelectedLocationState) {
        _selectedLocationState.update { currentState -> currentState.copy(
            latitude = selectedLocationState.latitude,
            longitude = selectedLocationState.longitude,
            locationName = selectedLocationState.locationName,
            currentLocation = selectedLocationState.currentLocation
        )}
    }
}
