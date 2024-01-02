package mobg.g58093.weather_app.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SearchResult(
    val locationName : String,
    val countryCode : String,
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



}