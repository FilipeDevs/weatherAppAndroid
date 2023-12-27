package mobg.g58093.weather_app.ui.forecast

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mobg.g58093.weather_app.SelectedLocationRepository

class ForecastViewModel(application: Application, private val selectedLocationRepository: SelectedLocationRepository) : AndroidViewModel(application) {

    private var selectedLocation = selectedLocationRepository.selectedLocationState.value

    private val TAG = "ForecastViewModel"

    init {
        viewModelScope.launch {
            observeSelectedCityState()
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



}

