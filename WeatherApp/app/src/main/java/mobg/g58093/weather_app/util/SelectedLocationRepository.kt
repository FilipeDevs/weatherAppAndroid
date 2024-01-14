package mobg.g58093.weather_app.util

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Data class representing the state of the selected location.
 */
data class SelectedLocationState(
    val locationName: String = "",
    val countryCode: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val currentLocation: Boolean = true,
)

/**
 * Singleton Repository for managing the state of the selected location.
 */
object SelectedLocationRepository {

    // selected location state.
    private val _selectedLocationState = MutableStateFlow(SelectedLocationState())

    val selectedLocationState: StateFlow<SelectedLocationState> =
        _selectedLocationState.asStateFlow()

    /**
     * Updates the selected location state.
     */
    fun editSelectLocation(selectedLocationState: SelectedLocationState) {
        Log.d("SelectedLocationRepository", "Location changed ! $selectedLocationState")
        _selectedLocationState.value = selectedLocationState
    }

    /**
     * Checks if the selected location state is empty. (no selected location)
     */
    fun isLocationStateEmpty(): Boolean {
        return _selectedLocationState.value == SelectedLocationState()
    }

}

