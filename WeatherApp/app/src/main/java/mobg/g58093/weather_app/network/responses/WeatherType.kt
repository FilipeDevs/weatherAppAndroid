package mobg.g58093.weather_app.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class WeatherType(
    val icon: String
)