package mobg.g58093.weather_app.ui.locations

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mobg.g58093.weather_app.util.SelectedLocationRepository
import mobg.g58093.weather_app.util.SelectedLocationState
import mobg.g58093.weather_app.data.WeatherEntry
import mobg.g58093.weather_app.data.WeatherRepository

data class Locations(
    val locationsList: List<WeatherEntry> = listOf()
)

class LocationsViewModel :
    ViewModel() {

    private val _locationsState = MutableStateFlow(Locations())
    val locationsState: StateFlow<Locations> = _locationsState.asStateFlow()

    /**
     * Initializes the LocationsViewModel. Initiates the process of getting all user locations.
     */
    init {
        viewModelScope.launch {
            getAllUserLocations()
        }
    }

    /**
     * Gets all user locations from the local database and updates the [locationsState] accordingly.
     */
    fun getAllUserLocations() {
        viewModelScope.launch {
            val weatherList = WeatherRepository.getAllWeatherEntries()
            _locationsState.update { currentState -> currentState.copy(locationsList = weatherList) }
        }
    }


    /**
     * Changes the selected location based on the provided [id].
     */
    fun changeSelectedLocation(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val weatherEntry = WeatherRepository.getWeatherEntry(id)
                if (weatherEntry != null) {
                    SelectedLocationRepository.editSelectLocation(
                        SelectedLocationState(
                            locationName = weatherEntry.locationName,
                            countryCode = weatherEntry.country,
                            longitude = weatherEntry.longitude,
                            latitude = weatherEntry.latitude,
                            currentLocation = weatherEntry.currentLocation
                        )
                    )
                }
            }
        }
    }

    /**
     * Selects another location when the current selected location is deleted.
     */
    private suspend fun selectAnotherLocation() {
        withContext(Dispatchers.IO) {
            val weatherList = WeatherRepository.getAllWeatherEntries()
            if (weatherList.isNotEmpty()) {
                val weatherEntry: WeatherEntry = weatherList[0]
                SelectedLocationRepository.editSelectLocation(
                    SelectedLocationState(
                        locationName = weatherEntry.locationName,
                        latitude = weatherEntry.latitude,
                        longitude = weatherEntry.longitude,
                        countryCode = weatherEntry.country,
                        currentLocation = weatherEntry.currentLocation
                    )
                )
            } else {
                SelectedLocationRepository.editSelectLocation(SelectedLocationState())
            }
        }
    }

    /**
     * Deletes the weather entry with the given [id]. If the selected location is the one being deleted,
     * selects another location.
     */
    fun deleteWeatherEntry(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val weatherEntry: WeatherEntry? = WeatherRepository.getWeatherEntry(id)

                // If selected location is the one being deleted, then select another location
                if (weatherEntry != null) {
                    WeatherRepository.deleteWeatherEntry(weatherEntry)
                    if (SelectedLocationRepository.selectedLocationState.value.latitude == weatherEntry.latitude &&
                        SelectedLocationRepository.selectedLocationState.value.longitude == weatherEntry.longitude
                    ) {
                        selectAnotherLocation()
                    }

                }
                getAllUserLocations()
            }
        }
    }
}