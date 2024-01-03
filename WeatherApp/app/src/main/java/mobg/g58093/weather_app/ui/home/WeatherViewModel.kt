package mobg.g58093.weather_app.ui.home

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mobg.g58093.weather_app.PropertiesManager
import mobg.g58093.weather_app.SelectedLocationRepository
import mobg.g58093.weather_app.SelectedLocationState
import mobg.g58093.weather_app.data.WeatherEntry
import mobg.g58093.weather_app.data.WeatherRepository
import mobg.g58093.weather_app.getCurrentLocation
import mobg.g58093.weather_app.hasLocationPermission
import mobg.g58093.weather_app.network.RetroApi
import mobg.g58093.weather_app.network.WeatherResponse
import mobg.g58093.weather_app.network.isOnline
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


sealed class WeatherApiState {
    object Loading : WeatherApiState()
    data class Success(val data: WeatherEntry) : WeatherApiState()
    data class Error(val message: String) : WeatherApiState()
}

class WeatherViewModel(application: Application, private val selectedLocationRepository: SelectedLocationRepository) : AndroidViewModel(application) {

    private val context = application

    // Some states
    private val _weatherState = MutableStateFlow<WeatherApiState>(WeatherApiState.Loading)
    val weatherState: StateFlow<WeatherApiState> = _weatherState

    private val _requestLocationPermission = MutableStateFlow(hasLocationPermission(context))
    val requestLocationPermission: StateFlow<Boolean> = _requestLocationPermission

    private val _selectedLocation = MutableStateFlow<SelectedLocationState>(SelectedLocationState())
    val selectedLocation: StateFlow<SelectedLocationState> = _selectedLocation

    private val apiKey = PropertiesManager.getApiKey(application)
    private val units = PropertiesManager.getUnits(application)

    private val TAG = "HomeViewModel"

    init {
        viewModelScope.launch {
            if(selectedLocation.value.currentLocation) { // current location selected
                fetchWeatherCurrentLocation()
            } else {
                getWeatherByCoordinates(
                    selectedLocation.value.latitude, selectedLocation.value.longitude,
                    false
                )
            }
            observeSelectedCityState()
            fetchWeather()
        }
    }

    fun fetchWeather() {
        getWeatherByCoordinates(44.34,
            10.99, false)
    }

    fun fetchWeatherCurrentLocation() {
        if(requestLocationPermission.value) { // location permissions check
            getCurrentLocation(context) { lat, long -> // fetch new current location
                getWeatherByCoordinates(
                    lat, long,  true
                )
            }
        } else { // location permissions not granted so try to fetch old current location or another location
            Log.d(TAG, "Location permissions not granted")
            fetchOldCurrentLocation()
        }
    }

    fun updatePermissions(perms : Boolean) {
        _requestLocationPermission.update { perms }
    }


    private fun fetchOldCurrentLocation() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // Fetch current location data on DB
                var currentLocationEntry : WeatherEntry? = WeatherRepository.getWeatherEntryCurrentLocation()
                if (currentLocationEntry != null) { // If db has data of current location
                    getWeatherByCoordinates(currentLocationEntry.latitude, currentLocationEntry.longitude, true)
                } else { // No data for current location, so try to load another location
                    currentLocationEntry = WeatherRepository.getFirstNonCurrentLocationEntry()
                    if(currentLocationEntry != null) {
                        getWeatherByCoordinates(currentLocationEntry.latitude,
                            currentLocationEntry.longitude, false)
                    } else {
                        _weatherState.value = WeatherApiState.Error("No locations found. Please " +
                                "add manually a location to view the weather.")
                    }
                }
            }
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

    private fun getWeatherByCoordinates(latitude: Double, longitude: Double, isCurrentLocation: Boolean) {
        viewModelScope.launch {
            try {
                _weatherState.value = WeatherApiState.Loading // Loading state
                if (isOnline(context)) { // Does the app have internet connection ?
                    // Weather api call to OpenWeather
                    val response = RetroApi.weatherService.getWeatherByCoordinates(latitude, longitude, units, apiKey)
                    // Run db operations
                    withContext(Dispatchers.IO) {
                        // Does the fetched location already exist on db (if true then it will return the actual id, otherwise id 0) ?
                        var existingWeatherEntryId = WeatherRepository.getWeatherEntry(response.coord.lat,
                            response.coord.lon)?.id ?: 0
                        if(isCurrentLocation) {
                            existingWeatherEntryId = WeatherRepository.getWeatherEntryCurrentLocation()?.id ?: 0
                        }
                        // Convert response to DTO
                        val weatherEntry = convertWeatherResponseToWeatherEntry(response, existingWeatherEntryId, isCurrentLocation)
                        // If entry exists, update it if not insert it
                        if (existingWeatherEntryId != 0) {
                            WeatherRepository.updateWeatherEntry(weatherEntry)
                        } else {
                            WeatherRepository.insertWeatherEntry(weatherEntry)
                        }
                        selectedLocationRepository.editSelectLocation(SelectedLocationState(
                            weatherEntry.locationName, weatherEntry.country ,weatherEntry.longitude,
                            weatherEntry.latitude, isCurrentLocation))
                        _weatherState.value = WeatherApiState.Success(weatherEntry)
                    }
                } else { // No internet connection
                    // Load old data from the local database
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show()
                    withContext(Dispatchers.IO) {
                        val localWeatherEntry : WeatherEntry?
                        if(isCurrentLocation) { // if the selected city is the current location
                            localWeatherEntry = WeatherRepository.getWeatherEntryCurrentLocation()
                        } else {
                            localWeatherEntry = WeatherRepository.getWeatherEntry(
                                selectedLocation.value.latitude,
                                selectedLocation.value.longitude)
                        }

                        if (localWeatherEntry != null) {
                            selectedLocationRepository.editSelectLocation(SelectedLocationState(
                                localWeatherEntry.locationName, localWeatherEntry.country,
                                localWeatherEntry.longitude, localWeatherEntry.latitude, isCurrentLocation))
                            _weatherState.value = WeatherApiState.Success(localWeatherEntry)
                        } else {
                            _weatherState.value = WeatherApiState.Error("No locations found. Please " +
                                    "add manually a location to view the weather.")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching data: ${e.message}")
                _weatherState.value = WeatherApiState.Error("Error fetching data: ${e.message}")
            }
        }
    }

    private fun convertCurrentDateToFormattedDate(): String {
        val date = Date()
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(date)
    }

    private fun convertUnixTimestampToHourAndMinutes(unixTimestamp: Long): String {
        val date = Date(unixTimestamp * 1000L)
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(date)
    }

    private fun convertWeatherResponseToWeatherEntry(weatherResponse: WeatherResponse, id : Int, isCurrentLocation: Boolean) : WeatherEntry {
        return WeatherEntry(
            id = id,
            locationName = weatherResponse.name,
            longitude = weatherResponse.coord.lon,
            latitude = weatherResponse.coord.lat,
            date = convertCurrentDateToFormattedDate(),
            mainTemp = weatherResponse.main.temp.toInt(),
            highTemp = weatherResponse.main.temp_max.toInt(),
            lowTemp = weatherResponse.main.temp_min.toInt(),
            weatherType = weatherResponse.weather[0].description,
            weatherIcon =  weatherResponse.weather[0].icon,
            sunriseHour =  convertUnixTimestampToHourAndMinutes(weatherResponse.sys.sunrise),
            sunsetHour =  convertUnixTimestampToHourAndMinutes(weatherResponse.sys.sunset),
            wind = weatherResponse.wind.speed,
            humidity =  weatherResponse.main.humidity,
            visibility = weatherResponse.visibility,
            pressure = weatherResponse.main.pressure,
            currentLocation = isCurrentLocation,
            country = weatherResponse.sys.country
        )
    }

    fun refreshData() {
        viewModelScope.launch {
            if(selectedLocation.value.currentLocation) {
                fetchWeatherCurrentLocation()
            } else {
                getWeatherByCoordinates(
                    selectedLocation.value.latitude,  selectedLocation.value.longitude,
                    selectedLocation.value.currentLocation)
            }

        }
    }

}

