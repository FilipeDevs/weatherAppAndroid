package mobg.g58093.weather_app.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class ForecastWeather(
    val dt: Long,
    val main: MainWeather,
    val weather: List<WeatherType>
)
