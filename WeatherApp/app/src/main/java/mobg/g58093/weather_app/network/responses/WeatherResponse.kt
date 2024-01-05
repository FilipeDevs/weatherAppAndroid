package mobg.g58093.weather_app.network.responses

import kotlinx.serialization.Serializable
import mobg.g58093.weather_app.network.responses.Coords

@Serializable
data class WeatherResponse(
    val coord: Coords,
    val main: MainWeatherInfo,
    val weather: List<WeatherInfo>,
    val sys: SysInfo,
    val wind: WindInfo,
    val visibility: Int,
    val name: String,
)










