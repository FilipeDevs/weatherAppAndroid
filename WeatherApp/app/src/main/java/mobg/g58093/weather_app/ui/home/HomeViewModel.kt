package mobg.g58093.weather_app.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
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
import mobg.g58093.weather_app.getUserLocation
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

class HomeViewModel(application: Application, private val selectedLocationRepository: SelectedLocationRepository) : AndroidViewModel(application) {

    private var selectedLocation = SelectedLocationState()

    private val context = application

    private val _weatherState = MutableStateFlow<WeatherApiState>(WeatherApiState.Loading)
    val weatherState: StateFlow<WeatherApiState> = _weatherState

    private val TAG = "HomeViewModel"

    init {
        viewModelScope.launch {
            /**
             * On launch, try to get weather for current location
             *      1. Check if app has permissions
             *      2. Check that GPS is activated
             *      3. If one of the checks fails, ask the user for permissions or activation of GPS
             *      4. If the user does not grant permissions or activate GPS
             *          4.1 Load weather data from db from his last stored current location
             *          4.2 If no data is found in DB for his current location, load weather data
             *              of the first favorite location
             *          4.3 If user does not have any locations, show an appropriate message
             **/
            if(selectedLocation.currentLocation) {
                getUserLocation(context) { locationState ->
                    if (locationState.isLocationPermissionGranted) {
                        getWeatherByCoordinates(
                            locationState.latitude, locationState.longitude, "metric",
                            "58701429b6088e321356701fde1e7ed0", true
                        )
                    } else {
                        getWeatherData()
                    }
                }
            } else {
                getWeatherByCoordinates(
                    selectedLocation.latitude, selectedLocation.longitude, "metric",
                    "58701429b6088e321356701fde1e7ed0", false
                )
            }

        }
        observeSelectedCityState()
    }

    private fun getWeatherData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // Fetch current location data on DB
                var currentLocationEntry : WeatherEntry? = WeatherRepository.getWeatherEntryCurrentLocation()
                if (currentLocationEntry != null) { // If db has data of current location
                    _weatherState.value = WeatherApiState.Success(currentLocationEntry)
                } else { // No data for current location, so load another location
                    currentLocationEntry = WeatherRepository.getFirstNonCurrentLocationEntry()
                    if(currentLocationEntry != null) {
                        getWeatherByCoordinates(currentLocationEntry.latitude, currentLocationEntry.longitude, "metric", "58701429b6088e321356701fde1e7ed0", false)
                    } else {
                        _weatherState.value = WeatherApiState.Error("You've got no locations to show the weather")
                    }
                }
            }
        }
    }

    private fun observeSelectedCityState() {
        viewModelScope.launch {
            selectedLocationRepository.selectedLocationState.collect { newState ->
                Log.d(TAG, "New selected location: $newState")
                selectedLocation =  newState
            }
        }
    }

    private fun getWeatherByCoordinates(latitude: Double, longitude: Double, units: String, apiKey: String, isCurrentLocation: Boolean) {
        viewModelScope.launch {
            try {
                _weatherState.value = WeatherApiState.Loading // Loading
                if (isOnline(context)) { // Does the app have internet connection ?
                    // Weather api call to OpenWeather
                    val response = RetroApi.weatherService.getWeatherByCoordinates(latitude, longitude, units, apiKey)
                    // Run db operations
                    withContext(Dispatchers.IO) {
                        // Does the fetched location already exist on db (if true then it will return the id, otherwise id 0) ?
                        val existingWeatherEntryId = WeatherRepository.getWeatherEntry(response.name)?.id ?: 0
                        // Convert response to DTO
                        val weatherEntry = convertWeatherResponseToWeatherEntry(response, existingWeatherEntryId, isCurrentLocation)
                        // If entry exists, update it if not insert it
                        if (existingWeatherEntryId != 0) {
                            WeatherRepository.updateWeatherEntry(weatherEntry)
                        } else {
                            WeatherRepository.insertWeatherEntry(weatherEntry)
                        }
                        selectedLocationRepository.editSelectLocation(SelectedLocationState(
                            weatherEntry.locationName, weatherEntry.longitude, weatherEntry.latitude, isCurrentLocation))
                        _weatherState.value = WeatherApiState.Success(weatherEntry)
                    }
                } else { // No internet connection
                    // Load data from the local database
                    withContext(Dispatchers.IO) {
                        val localWeatherEntry = WeatherRepository.getWeatherEntry(selectedLocation.locationName)
                        if (localWeatherEntry != null) {
                            selectedLocationRepository.editSelectLocation(SelectedLocationState(
                                localWeatherEntry.locationName, localWeatherEntry.longitude, localWeatherEntry.latitude, isCurrentLocation))
                            _weatherState.value = WeatherApiState.Success(localWeatherEntry)
                        } else {
                            _weatherState.value = WeatherApiState.Error("No internet connection and no data in the database.")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching data: ${e.message}")
                _weatherState.value = WeatherApiState.Error("Error fetching data: ${e.message}")
            }
        }
    }

    fun convertCurrentDateToFormattedDate(): String {
        val date = Date()
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(date)
    }

    fun convertUnixTimestampToHourAndMinutes(unixTimestamp: Long): String {
        val date = Date(unixTimestamp * 1000L)
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(date)
    }

    fun convertWeatherResponseToWeatherEntry(weatherResponse: WeatherResponse, id : Int, isCurrentLocation: Boolean) : WeatherEntry {
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
        )
    }

    fun refreshData() {
        viewModelScope.launch {
            getWeatherByCoordinates(
                selectedLocation.latitude,  selectedLocation.longitude, "metric",
                    "58701429b6088e321356701fde1e7ed0", selectedLocation.currentLocation)
        }
    }

}

