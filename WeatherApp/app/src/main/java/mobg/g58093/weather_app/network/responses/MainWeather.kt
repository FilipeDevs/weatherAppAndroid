package mobg.g58093.weather_app.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class MainWeather(
    val temp: Double,
    val humidity: Int
)
