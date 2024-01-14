package mobg.g58093.weather_app.ui.home

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mobg.g58093.weather_app.data.WeatherEntry
import mobg.g58093.weather_app.data.WeatherRepository
import mobg.g58093.weather_app.network.RetroApi
import mobg.g58093.weather_app.network.isOnline
import mobg.g58093.weather_app.network.responses.WeatherResponse
import mobg.g58093.weather_app.util.LocationPermissionsAndGPSRepository
import mobg.g58093.weather_app.util.PropertiesManager
import mobg.g58093.weather_app.util.SelectedLocationRepository
import mobg.g58093.weather_app.util.SelectedLocationState
import mobg.g58093.weather_app.util.getCurrentLocation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


sealed class WeatherApiState {
    object Loading : WeatherApiState()
    data class Success(val data: WeatherEntry) : WeatherApiState()
    data class Error(val message: String) : WeatherApiState()
}

class WeatherViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val context = application
    private val apiKey = PropertiesManager.getApiKey(application)
    private val units = PropertiesManager.getUnits(application)
    private val TAG = "HomeViewModel"

    // Main data
    private val _weatherState = MutableStateFlow<WeatherApiState>(WeatherApiState.Loading)
    val weatherState: StateFlow<WeatherApiState> = _weatherState

    // Flag of permissions request (only happens on launch)
    private val _firstLaunchPerms = MutableStateFlow(false)
    val firstLaunchPerms: StateFlow<Boolean> = _firstLaunchPerms


    /**
     * Initializes the WeatherViewModel. Initiates the weather data retrieval based on the selected location.
     */
    init {
        viewModelScope.launch {
            // current location selected
            if (SelectedLocationRepository.selectedLocationState.value.currentLocation) {
                fetchWeatherCurrentLocation()
            } else {
                getWeatherByCoordinates(
                    SelectedLocationRepository.selectedLocationState.value.latitude,
                    SelectedLocationRepository.selectedLocationState.value.longitude,
                    false
                )
            }

        }
        viewModelScope.launch {
            observePermissionsState() // Listen to changes to permissions
        }
        viewModelScope.launch {
            observeSelectedCityState() // Listen to changes to selected location
        }
    }

    /**
     * Updates the flag indicating that the permissions have been requested for the first time.
     */
    fun updateFirstLaunchPermissions() {
        _firstLaunchPerms.update { true }
    }

    /**
     * Fetches weather data for the current location.
     */
    fun fetchWeatherCurrentLocation() {
        LocationPermissionsAndGPSRepository.refreshChecks(context)

        if (LocationPermissionsAndGPSRepository.permissions.value) {
            if (LocationPermissionsAndGPSRepository.gps.value) {
                getCurrentLocation(context) { lat, long -> // fetch new current location
                    getWeatherByCoordinates(
                        lat, long, true
                    )
                }
            } else { // GPS not enabled, try to fetch old current location
                fetchOldCurrentLocation()
            }

        } else { // Permission not granted, try to fetch old current location
            Log.d(TAG, "Location permissions not granted")
            fetchOldCurrentLocation()
        }
    }

    /**
     * Updates the location permission status.
     */
    fun updatePermissions() {
        LocationPermissionsAndGPSRepository.refreshChecks(context)
    }

    /**
     * Fetches the old weather data for the current location if GPS is not enabled or permission is not granted.
     */
    private fun fetchOldCurrentLocation() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // Fetch current location data on DB
                var currentLocationEntry: WeatherEntry? =
                    WeatherRepository.getWeatherEntryCurrentLocation()
                if (currentLocationEntry != null) { // If DB has data of current location
                    getWeatherByCoordinates(
                        currentLocationEntry.latitude, currentLocationEntry.longitude, true
                    )
                } else { // No data for current location, so try to load another location
                    currentLocationEntry = WeatherRepository.getFirstNonCurrentLocationEntry()
                    if (currentLocationEntry != null) {
                        getWeatherByCoordinates(
                            currentLocationEntry.latitude, currentLocationEntry.longitude, false
                        )
                    } else { // No locations on DB, user has to manually add one
                        Log.d(TAG, "Here ")
                        _weatherState.value = WeatherApiState.Error(
                            "No locations found. Please add manually a location to view the weather."
                        )
                    }
                }
            }
        }
    }

    /**
     * Observes changes to the selected location state and updates the weather data accordingly.
     */
    private suspend fun observeSelectedCityState() {
        SelectedLocationRepository.selectedLocationState.collect { newLocationState ->
            if (!SelectedLocationRepository.isLocationStateEmpty()) {
                getWeatherByCoordinates(
                    newLocationState.latitude,
                    newLocationState.longitude,
                    newLocationState.currentLocation,
                )
            } else {
                _weatherState.value = WeatherApiState.Error(
                    "No locations found. Please add manually a location to view the weather."
                )
            }

        }
    }

    /**
     * Observes changes to the permissions state and fetches weather data for the current location if permissions are granted.
     */
    private suspend fun observePermissionsState() {
        LocationPermissionsAndGPSRepository.permissions.collect { newPermissionsState ->
            Log.d(TAG, "Permissions changed : $newPermissionsState")
            if(newPermissionsState && SelectedLocationRepository.selectedLocationState.value.currentLocation) {
                fetchWeatherCurrentLocation()
            }
        }
    }

    private fun getWeatherByCoordinates(
        latitude: Double, longitude: Double, isCurrentLocation: Boolean
    ) {
        viewModelScope.launch {
            try {
                _weatherState.value = WeatherApiState.Loading // Loading state
                if (isOnline(context)) {

                    // Weather API call to OpenWeather
                    val response = RetroApi.weatherService.getWeatherByCoordinates(
                        latitude, longitude, units, apiKey
                    )

                    // Run DB operations
                    withContext(Dispatchers.IO) {
                        val existingWeatherEntryId = getExistingWeatherEntryId(
                            response.coord.lat, response.coord.lon, isCurrentLocation
                        )
                        val weatherEntry = convertWeatherResponseToWeatherEntry(
                            response, existingWeatherEntryId, isCurrentLocation
                        )

                        // If entry exists, update it, if not, insert it
                        if (existingWeatherEntryId != 0) {
                            WeatherRepository.updateWeatherEntry(weatherEntry)
                        } else {
                            WeatherRepository.insertWeatherEntry(weatherEntry)
                        }

                        updateSelectedLocation(weatherEntry, isCurrentLocation)
                        _weatherState.value = WeatherApiState.Success(weatherEntry)
                    }
                } else {
                    // No internet connection, load old data from DB
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show()
                    loadOldDataFromDB(isCurrentLocation)
                }
            } catch (e: Exception) {
                _weatherState.value = WeatherApiState.Error("Error fetching data: ${e.message}")
            }
        }

    }

    /**
     * Gets the existing weather entry ID for the given coordinates and current location status.
     * @return The existing weather entry ID, or 0 if not found.
     */
    private suspend fun getExistingWeatherEntryId(
        latitude: Double, longitude: Double, isCurrentLocation: Boolean
    ): Int {
        return WeatherRepository.getWeatherEntry(
            latitude, longitude
        )?.id ?: if (isCurrentLocation) WeatherRepository.getWeatherEntryCurrentLocation()?.id
            ?: 0 else 0
    }

    /**
     * Loads old weather data from the database for the specified location.
     */
    private suspend fun loadOldDataFromDB(isCurrentLocation: Boolean) {
        withContext(Dispatchers.IO) {
            val localWeatherEntry: WeatherEntry? = if (isCurrentLocation) {
                WeatherRepository.getWeatherEntryCurrentLocation()
            } else {
                WeatherRepository.getWeatherEntry(
                    SelectedLocationRepository.selectedLocationState.value.latitude,
                    SelectedLocationRepository.selectedLocationState.value.longitude,
                )
            }

            if (localWeatherEntry != null) {
                updateSelectedLocation(localWeatherEntry, isCurrentLocation)
                _weatherState.value = WeatherApiState.Success(localWeatherEntry)
            } else {
                _weatherState.value = WeatherApiState.Error(
                    "No locations found. Please " + "add manually a location to view the weather."
                )
            }
        }
    }

    /**
     * Updates the selected location in the repository based on the provided weather entry and location status.
     */
    private fun updateSelectedLocation(weatherEntry: WeatherEntry, isCurrentLocation: Boolean) {
        SelectedLocationRepository.editSelectLocation(
            SelectedLocationState(
                weatherEntry.locationName,
                weatherEntry.country,
                weatherEntry.longitude,
                weatherEntry.latitude,
                isCurrentLocation
            )
        )
    }

    private fun convertCurrentDateToFormattedDate(): String {
        val date = Date()
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(date)
    }

    /**
     * Converts a Unix timestamp to a formatted string representing the hour and minutes.
     */
    private fun convertUnixTimestampToHourAndMinutes(
        unixTimestamp: Long,
        timeZoneOffsetSeconds: Int
    ): String {
        val date = Date(unixTimestamp * 1000L)
        val adjustedTime = date.time + timeZoneOffsetSeconds * 1000L
        val adjustedDate = Date(adjustedTime)
        val sdf = SimpleDateFormat("HH:mm", Locale.US)

        return sdf.format(adjustedDate)
    }

    /**
     * Converts a weather API response to a WeatherEntry object.
     */
    private fun convertWeatherResponseToWeatherEntry(
        weatherResponse: WeatherResponse, id: Int, isCurrentLocation: Boolean
    ): WeatherEntry {
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
            weatherIcon = weatherResponse.weather[0].icon,
            sunriseHour = convertUnixTimestampToHourAndMinutes(
                weatherResponse.sys.sunrise,
                weatherResponse.timezone
            ),
            sunsetHour = convertUnixTimestampToHourAndMinutes(
                weatherResponse.sys.sunset,
                weatherResponse.timezone
            ),
            wind = weatherResponse.wind.speed,
            humidity = weatherResponse.main.humidity,
            visibility = weatherResponse.visibility,
            pressure = weatherResponse.main.pressure,
            currentLocation = isCurrentLocation,
            country = weatherResponse.sys.country
        )
    }

    /**
     * Refreshes weather data based on the current selected location.
     */
    fun refreshData() {
        viewModelScope.launch {
            if (SelectedLocationRepository.selectedLocationState.value.currentLocation) {
                fetchWeatherCurrentLocation()
            } else {
                getWeatherByCoordinates(
                    SelectedLocationRepository.selectedLocationState.value.latitude,
                    SelectedLocationRepository.selectedLocationState.value.longitude,
                    SelectedLocationRepository.selectedLocationState.value.currentLocation
                )
            }

        }
    }

}

