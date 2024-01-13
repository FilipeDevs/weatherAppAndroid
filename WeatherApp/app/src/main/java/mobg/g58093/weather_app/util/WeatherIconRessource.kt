package mobg.g58093.weather_app.util

import mobg.g58093.weather_app.R

fun getDynamicResourceId(weatherIcon: String): Int {
    val resourceId = "_$weatherIcon"
    return try {
        R.drawable::class.java.getField(resourceId).getInt(null)
    } catch (e: Exception) {
        R.drawable._01d
    }
}