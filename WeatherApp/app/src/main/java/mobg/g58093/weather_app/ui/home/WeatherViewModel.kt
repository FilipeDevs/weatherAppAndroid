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
import mobg.g58093.weather_app.util.PropertiesManager
import mobg.g58093.weather_app.util.SelectedLocationRepository
import mobg.g58093.weather_app.util.SelectedLocationState
import mobg.g58093.weather_app.util.checkIsGPSEnabled
import mobg.g58093.weather_app.data.WeatherEntry
import mobg.g58093.weather_app.data.WeatherRepository
import mobg.g58093.weather_app.util.getCurrentLocation
import mobg.g58093.weather_app.util.hasLocationPermission
import mobg.g58093.weather_app.network.RetroApi
import mobg.g58093.weather_app.network.responses.WeatherResponse
import mobg.g58093.weather_app.network.isOnline
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


sealed class WeatherApiState {
    object Loading : WeatherApiState()
    data class Success(val data: WeatherEntry) : WeatherApiState()
    data class Error(val message: String) : WeatherApiState()
}

class WeatherViewModel(
    application: Application, private val selectedLocationRepository: SelectedLocationRepository
) : AndroidViewModel(application) {

    private val context = application
    private val apiKey = PropertiesManager.getApiKey(application)
    private val units = PropertiesManager.getUnits(application)
    private val TAG = "HomeViewModel"

    // Main data
    private val _weatherState = MutableStateFlow<WeatherApiState>(WeatherApiState.Loading)
    val weatherState: StateFlow<WeatherApiState> = _weatherState

    // Current selected location
    private val _selectedLocation = MutableStateFlow(SelectedLocationState())
    val selectedLocation: StateFlow<SelectedLocationState> = _selectedLocation

    // Permissions
    private val _locationPermission = MutableStateFlow(true)
    val locationPermission: StateFlow<Boolean> = _locationPermission

    // GPS
    private val _gpsStatus = MutableStateFlow(true)
    val gpsStatus: StateFlow<Boolean> = _gpsStatus

    // Flag of permissions request (only happens on launch)
    private val _firstLaunchPerms = MutableStateFlow(false)
    val firstLaunchPerms: StateFlow<Boolean> = _firstLaunchPerms


    init {
        viewModelScope.launch {
            if (selectedLocation.value.currentLocation) { // current location selected
                fetchWeatherCurrentLocation()
            } else {
                getWeatherByCoordinates(
                    selectedLocation.value.latitude, selectedLocation.value.longitude, false
                )
            }
            observeSelectedCityState() // Listen to changes to selected location
        }
    }

    fun updateFirstLaunchPermissions() {
        _firstLaunchPerms.update { true }
    }

    fun fetchWeatherCurrentLocation() {
        // Check location permissions and GPS status
        _locationPermission.value = hasLocationPermission(context)
        _gpsStatus.value = checkIsGPSEnabled(context)

        if (locationPermission.value) {
            if (gpsStatus.value) {
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

    fun updatePermissions(perms: Boolean) {
        _locationPermission.update { perms }
    }

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
                    } else {
                        _weatherState.value = WeatherApiState.Error(
                            "No locations found. Please add manually a location to view the weather."
                        )
                    }
                }
            }
        }
    }

    private suspend fun observeSelectedCityState() {
        selectedLocationRepository.selectedLocationState.collect { newState ->
            if (!selectedLocationRepository.isLocationStateEmpty()) {
                _selectedLocation.update { newState }
                getWeatherByCoordinates(
                    selectedLocation.value.latitude,
                    selectedLocation.value.longitude, selectedLocation.value.currentLocation
                )
            } else {
                _weatherState.value = WeatherApiState.Error(
                    "No locations found. Please add manually a location to view the weather."
                )
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

    private suspend fun getExistingWeatherEntryId(
        latitude: Double, longitude: Double, isCurrentLocation: Boolean
    ): Int {
        return WeatherRepository.getWeatherEntry(
            latitude, longitude
        )?.id ?: if (isCurrentLocation) WeatherRepository.getWeatherEntryCurrentLocation()?.id
            ?: 0 else 0
    }

    private suspend fun loadOldDataFromDB(isCurrentLocation: Boolean) {
        withContext(Dispatchers.IO) {
            val localWeatherEntry: WeatherEntry? = if (isCurrentLocation) {
                WeatherRepository.getWeatherEntryCurrentLocation()
            } else {
                WeatherRepository.getWeatherEntry(
                    selectedLocation.value.latitude, selectedLocation.value.longitude
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

    private fun updateSelectedLocation(weatherEntry: WeatherEntry, isCurrentLocation: Boolean) {
        selectedLocationRepository.editSelectLocation(
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

    private fun convertUnixTimestampToHourAndMinutes(
        unixTimestamp: Long,
        timeZoneOffsetSeconds: Int
    ): String {
        val date = Date(unixTimestamp * 1000L)
        val adjustedTime = date.time + timeZoneOffsetSeconds * 1000L
        val adjustedDate = Date(adjustedTime)
        val sdf = SimpleDateFormat("HH:mm")

        sdf.timeZone = TimeZone.getTimeZone("GMT")

        return sdf.format(adjustedDate)
    }

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

    fun refreshData() {
        viewModelScope.launch {
            if (selectedLocation.value.currentLocation) {
                fetchWeatherCurrentLocation()
            } else {
                getWeatherByCoordinates(
                    selectedLocation.value.latitude,
                    selectedLocation.value.longitude,
                    selectedLocation.value.currentLocation
                )
            }

        }
    }

}

