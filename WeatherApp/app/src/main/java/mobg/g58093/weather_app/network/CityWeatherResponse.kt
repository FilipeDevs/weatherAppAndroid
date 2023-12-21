package mobg.g58093.weather_app.network

import kotlinx.serialization.Serializable

@Serializable
data class CityWeatherResponse(
    val name: String,
    val lat: Int,
    val lot: Int,
)