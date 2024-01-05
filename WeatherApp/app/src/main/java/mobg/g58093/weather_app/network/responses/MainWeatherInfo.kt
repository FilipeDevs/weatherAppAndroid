package mobg.g58093.weather_app.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class MainWeatherInfo(
    val temp: Double,
    val temp_min: Double,
    val temp_max: Double,
    val humidity: Int,
    val pressure: Int
)
