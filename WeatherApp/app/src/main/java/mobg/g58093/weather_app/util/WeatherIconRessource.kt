package mobg.g58093.weather_app.util

import mobg.g58093.weather_app.R

/**
 * Gets the dynamic resource ID for a given weather icon.
 */
fun getDynamicResourceId(weatherIcon: String): Int {
    val resourceId = "_$weatherIcon"
    return try {
        R.drawable::class.java.getField(resourceId).getInt(null)
    } catch (e: Exception) {
        R.drawable._01d
    }
}