package mobg.g58093.weather_app

import android.content.Context
import java.util.Properties

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