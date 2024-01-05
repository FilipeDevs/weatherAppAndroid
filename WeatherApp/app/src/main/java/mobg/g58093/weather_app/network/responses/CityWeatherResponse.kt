package mobg.g58093.weather_app.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class LocationWeatherResponse(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String = "", // state not always given
)