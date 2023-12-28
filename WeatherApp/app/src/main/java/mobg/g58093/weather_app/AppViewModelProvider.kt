package mobg.g58093.weather_app

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import mobg.g58093.weather_app.ui.forecast.ForecastViewModel
import mobg.g58093.weather_app.ui.home.WeatherViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for ItemEditViewModel
        initializer {
            val appContainer = this.weatherApplication().container
            WeatherViewModel(appContainer.application, appContainer.userRepository)
        }
        initializer {
            val appContainer = this.weatherApplication().container
            ForecastViewModel(appContainer.application, appContainer.userRepository)
        }

    }
}


fun CreationExtras.weatherApplication(): WeatherApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as WeatherApplication)
