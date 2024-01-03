package mobg.g58093.weather_app.ui.search

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mobg.g58093.weather_app.PropertiesManager
import mobg.g58093.weather_app.network.RetroApi
import mobg.g58093.weather_app.network.isOnline
import mobg.g58093.weather_app.ui.home.WeatherApiState

data class SearchResult(
    val locationName : String,
    val countryCode : String,
    val state : String = "",
    val longitude : Double,
    val latitude : Double
)

sealed class SearchApiState {
    object Loading : SearchApiState()
    data class Success(val data: List<SearchResult>) : SearchApiState()
    data class Error(val message: String) : SearchApiState()
}

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application

    // Some states
    private val _searchState = MutableStateFlow<SearchApiState>(SearchApiState.Loading)
    val searchState: StateFlow<SearchApiState> = _searchState

    private val apiKey = PropertiesManager.getApiKey(application)

    private val TAG = "SearchViewModel"

    fun searchLocation(searchQuery : String) {
        viewModelScope.launch {
            _searchState.value = SearchApiState.Loading
            if(isOnline(context)) { // Check internet connection
                try {
                    val response = RetroApi.weatherService.getCityWeather(searchQuery, 5,apiKey)
                } catch (e: Exception) {
                    Log.e(TAG, "Encountered an error : ${e.message}")
                    _searchState.value = SearchApiState.Error("An unexpected error occurred")
                }
            } else {
                Log.e(TAG, "No internet connection...")
                _searchState.value = SearchApiState.Error("Unable to search locations due to lack " +
                        "of internet connection")
            }
        }
    }

}