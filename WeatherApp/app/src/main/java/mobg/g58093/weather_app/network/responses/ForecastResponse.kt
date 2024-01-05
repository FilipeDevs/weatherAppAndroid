package mobg.g58093.weather_app.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponse(
    val list: List<ForecastWeather>,
    val city: City,
)

