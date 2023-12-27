package mobg.g58093.weather_app

import android.app.Application
import android.content.Context

interface AppContainer {
    val application: WeatherApplication
    val userRepository: SelectedLocationRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val application: WeatherApplication by lazy {
        context.applicationContext as WeatherApplication
    }

    override val userRepository: SelectedLocationRepository by lazy {
        SelectedLocationRepository()
    }
}

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
