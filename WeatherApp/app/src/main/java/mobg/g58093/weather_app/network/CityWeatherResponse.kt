package mobg.g58093.weather_app.network

import kotlinx.serialization.Serializable

@Serializable
data class LocationWeatherResponse(
    val name: String,
    val lat: Double,
    val lot: Double,
    val country : String,
    val state : String = "", // not always present
)