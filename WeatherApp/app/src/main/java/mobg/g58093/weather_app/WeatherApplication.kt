package mobg.g58093.weather_app

import android.app.Application
import android.content.Context

/**
 * Interface defining the contract for the AppContainer, providing access to the WeatherApplication instance.
 */
interface AppContainer {
    val application: WeatherApplication
}

/**
 * Implementation of the AppContainer interface, utilizing lazy initialization for the WeatherApplication instance.
 */
class AppDataContainer(private val context: Context) : AppContainer {

    override val application: WeatherApplication by lazy {
        context.applicationContext as WeatherApplication
    }

}

/**
 * Custom Application class representing the main entry point of the application.
 * It initializes the AppContainer for dependency injection.
 */
class WeatherApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
