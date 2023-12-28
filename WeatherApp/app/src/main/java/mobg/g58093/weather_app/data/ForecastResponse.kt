package mobg.g58093.weather_app.data

import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponse(
    val timezone : String,
    val list: List<ForecastWeather>,
    val city : City,
)

@Serializable
data class City(
    val name : String
)

@Serializable
data class ForecastWeather(
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

