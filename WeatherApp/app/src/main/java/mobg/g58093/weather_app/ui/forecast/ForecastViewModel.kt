package mobg.g58093.weather_app.ui.forecast

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mobg.g58093.weather_app.ui.common.SelectedCityViewModel

class ForecastViewModel(application: Application) : AndroidViewModel(application) {



    private val TAG = "ForecastViewModel"

    init {
        viewModelScope.launch {

        }
    }

    fun logState() {

    }


}

