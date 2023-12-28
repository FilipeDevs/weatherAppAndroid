package mobg.g58093.weather_app.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import mobg.g58093.weather_app.data.ForecastResponse
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    // /geo/1.0/direct endpoint
    @GET("geo/1.0/direct")
    suspend fun getCityWeather(
        @Query("q") cityName: String,
        @Query("limit") limit: Int,
        @Query("appid") apiKey: String
    ): CityWeatherResponse

    // /data/2.5/weather endpoint
    @GET("data/2.5/weather")
    suspend fun getWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units : String,
        @Query("appid") apiKey: String
    ): WeatherResponse

    @GET("forecast/daily")
    suspend fun getWeatherForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units : String,
        @Query("cnt") cnt: Int,
        @Query("appid") apiKey: String
    ): ForecastResponse
}

object RetroApi {
    private const val BASE_URL = "https://api.openweathermap.org/"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    val weatherService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(BASE_URL)
        .build()

}