package mobg.g58093.weather_app.data

import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponse(
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
    val main: MainWeather,
    val weather : List<WeatherType>
)

@Serializable
data class MainWeather(
    val temp_min: Double,
    val temp_max: Double,
    val humidity : Int
)
@Serializable
data class WeatherType (
    val icon : String
)

