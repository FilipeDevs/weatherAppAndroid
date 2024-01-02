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
import mobg.g58093.weather_app.SelectedLocationRepository
import mobg.g58093.weather_app.SelectedLocationState
import mobg.g58093.weather_app.data.WeatherEntry
import mobg.g58093.weather_app.data.WeatherRepository

data class Locations(
    val locationsList : List<WeatherEntry> = listOf()
)

class LocationsViewModel(private val selectedLocationRepository: SelectedLocationRepository) : ViewModel() {

    private val _locationsState = MutableStateFlow<Locations>(Locations())
    val locationsState: StateFlow<Locations> = _locationsState.asStateFlow()

    private val _selectedLocation = MutableStateFlow<SelectedLocationState>(SelectedLocationState())
    val selectedLocation: StateFlow<SelectedLocationState> = _selectedLocation

    private val TAG = "LocationsViewModel"


    init {
        viewModelScope.launch {
            observeSelectedCityState()
        }
    }

    fun getAllUserLocations() {
        viewModelScope.launch {
            val weatherList = WeatherRepository.getAllWeatherEntries()
            _locationsState.update { currentState -> currentState.copy(locationsList =  weatherList) }
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

    fun changeSelectedLocation(id : Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val weatherEntry = WeatherRepository.getWeatherEntry(id)
                if (weatherEntry != null) {
                    selectedLocationRepository.editSelectLocation(SelectedLocationState(
                        locationName = weatherEntry.locationName,
                        countryCode = weatherEntry.country,
                        longitude = weatherEntry.longitude,
                        latitude = weatherEntry.latitude,
                        currentLocation = weatherEntry.currentLocation
                    ))
                }
            }
        }
    }

    fun deleteWeatherEntry(id : Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val weatherEntry : WeatherEntry? = WeatherRepository.getWeatherEntry(id)

                // If selected location is the one being deleted, then reset the selected location state
                if (weatherEntry != null) {
                    if(selectedLocation.value.locationName == weatherEntry.locationName &&
                        selectedLocation.value.countryCode == weatherEntry.country) {
                            selectedLocationRepository.resetSelectedLocation()
                    }
                    WeatherRepository.deleteWeatherEntry(weatherEntry)
                }
                getAllUserLocations()
            }
        }
    }
}