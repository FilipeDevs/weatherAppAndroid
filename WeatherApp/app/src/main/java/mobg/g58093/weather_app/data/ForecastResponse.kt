package mobg.g58093.weather_app.data

import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponse(
    val timezone : String,
    val list: List<ForecastWeatherEntry>
)

@Serializable
data class ForecastWeatherEntry(
    val dt: Long,
    val temp: Temperature,
    val humidity: Int,
    val weather : WeatherType
)

@Serializable
data class Temperature(
    val min: Double,
    val max: Double,
)
@Serializable
data class WeatherType (
    val icon : String
)

