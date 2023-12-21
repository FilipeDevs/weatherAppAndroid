package mobg.g58093.weather_app.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val main : MainWeatherInfo,
    val weather: List<WeatherInfo>,
    val sys: SysInfo,
   val wind: WindInfo,
    val visibility: Int,
   val rain: RainInfo?, // Nullable because "rain" field may not always be present
)

@Serializable
data class MainWeatherInfo(
    val temp: Double,
    val temp_min: Double,
    val temp_max: Double,
    val humidity: Int,
    val pressure: Int
)

@Serializable
data class WeatherInfo(
    val description: String,
    val icon: String
)

@Serializable
data class SysInfo(
    val sunrise: Long,
    val sunset: Long
)

@Serializable
data class WindInfo(
    val speed: Double
)

@Serializable
data class RainInfo(
    @SerialName("1h")
    val oneHour: Double
)