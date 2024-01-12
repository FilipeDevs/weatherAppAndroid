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

class LocationsViewModel(private val selectedLocationRepository: SelectedLocationRepository) :
    ViewModel() {

    private val _locationsState = MutableStateFlow<Locations>(Locations())
    val locationsState: StateFlow<Locations> = _locationsState.asStateFlow()

    private val _selectedLocation = MutableStateFlow<SelectedLocationState>(SelectedLocationState())
    val selectedLocation: StateFlow<SelectedLocationState> = _selectedLocation

    private val TAG = "LocationsViewModel"


    init {
        viewModelScope.launch {
            getAllUserLocations()
            observeSelectedCityState()
        }
    }

    fun getAllUserLocations() {
        viewModelScope.launch {
            val weatherList = WeatherRepository.getAllWeatherEntries()
            _locationsState.update { currentState -> currentState.copy(locationsList = weatherList) }
        }
    }

    private fun observeSelectedCityState() {
        viewModelScope.launch {
            selectedLocationRepository.selectedLocationState.collect { newState ->
                Log.d(TAG, "New selected location: $newState")
                _selectedLocation.update { newState }
            }
        }
    }

    fun changeSelectedLocation(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val weatherEntry = WeatherRepository.getWeatherEntry(id)
                if (weatherEntry != null) {
                    selectedLocationRepository.editSelectLocation(
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

    private suspend fun selectAnotherLocation() {
        withContext(Dispatchers.IO) {
            val weatherList = WeatherRepository.getAllWeatherEntries()
            if (weatherList.isNotEmpty()) {
                val weatherEntry: WeatherEntry = weatherList[0]
                selectedLocationRepository.editSelectLocation(
                    SelectedLocationState(
                        locationName = weatherEntry.locationName,
                        latitude = weatherEntry.latitude,
                        longitude = weatherEntry.longitude,
                        countryCode = weatherEntry.country,
                        currentLocation = weatherEntry.currentLocation
                    )
                )
            } else {
                selectedLocationRepository.editSelectLocation(SelectedLocationState(isNull = true))
            }
        }
    }

    fun deleteWeatherEntry(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val weatherEntry: WeatherEntry? = WeatherRepository.getWeatherEntry(id)

                // If selected location is the one being deleted, then select another location
                if (weatherEntry != null) {
                    WeatherRepository.deleteWeatherEntry(weatherEntry)
                    if (selectedLocation.value.latitude == weatherEntry.latitude &&
                        selectedLocation.value.longitude == weatherEntry.longitude
                    ) {
                        selectAnotherLocation()
                    }

                }
                getAllUserLocations()
            }
        }
    }
}