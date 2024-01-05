package mobg.g58093.weather_app.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class SysInfo(
    val sunrise: Long,
    val sunset: Long,
    val country: String,
)
