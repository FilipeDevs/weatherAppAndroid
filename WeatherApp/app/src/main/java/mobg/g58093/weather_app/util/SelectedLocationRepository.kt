package mobg.g58093.weather_app.util

import android.util.Log
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

object SelectedLocationRepository {
    private val _selectedLocationState = MutableStateFlow(SelectedLocationState())
    val selectedLocationState: StateFlow<SelectedLocationState> =
        _selectedLocationState.asStateFlow()

    fun editSelectLocation(selectedLocationState: SelectedLocationState) {
        Log.d("SelectedLocationRepository", "Location changed ! $selectedLocationState")
        _selectedLocationState.value = selectedLocationState
    }

    fun isLocationStateEmpty(): Boolean {
        return _selectedLocationState.value == SelectedLocationState()
    }

}

