package mobg.g58093.weather_app

import android.app.Application
import android.content.Context

interface AppContainer {
    val application: WeatherApplication
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val application: WeatherApplication by lazy {
        context.applicationContext as WeatherApplication
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
