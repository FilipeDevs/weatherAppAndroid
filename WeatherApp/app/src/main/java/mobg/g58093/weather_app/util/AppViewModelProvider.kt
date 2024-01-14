package mobg.g58093.weather_app.util

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import mobg.g58093.weather_app.WeatherApplication
import mobg.g58093.weather_app.ui.forecast.ForecastViewModel
import mobg.g58093.weather_app.ui.home.WeatherViewModel
import mobg.g58093.weather_app.ui.locations.LocationsViewModel
import mobg.g58093.weather_app.ui.search.SearchViewModel


/**
 * Object responsible for providing ViewModel instances using a custom factory.
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val appContainer = this.weatherApplication().container
            WeatherViewModel(appContainer.application)
        }
        initializer {
            val appContainer = this.weatherApplication().container
            ForecastViewModel(appContainer.application)
        }
        initializer {
            LocationsViewModel()
        }
        initializer {
            val appContainer = this.weatherApplication().container
            SearchViewModel(appContainer.application)
        }

    }
}


/**
 * Extension function for CreationExtras that retrieves the WeatherApplication instance.
 */
fun CreationExtras.weatherApplication(): WeatherApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as WeatherApplication)
