package mobg.g58093.weather_app.util

import android.content.Context
import mobg.g58093.weather_app.R
import java.util.Properties

/**
 * Singleton object responsible for managing application properties.
 */
object PropertiesManager {

    fun getApiKey(context: Context): String {
        val inputStream = context.resources.openRawResource(R.raw.app)
        val properties = Properties()
        properties.load(inputStream)
        return properties.getProperty("api_key") ?: ""
    }

    fun getUnits(context: Context) : String {
        val inputStream = context.resources.openRawResource(R.raw.app)
        val properties = Properties()
        properties.load(inputStream)
        return properties.getProperty("units") ?: "metric"
    }
}