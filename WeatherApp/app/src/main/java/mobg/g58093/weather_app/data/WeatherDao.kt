package mobg.g58093.weather_app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWeatherEntry(weather: WeatherEntry)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertForecastEntry(forecast: ForecastEntry)

    @Query("SELECT * from main_weather")
    suspend fun getAllWeatherEntries(): List<WeatherEntry>

    @Query("SELECT * from forecast_weather WHERE weatherEntryId = :id")
    suspend fun getAllForecastEntriesByWeather(id: Int): List<ForecastEntry>

    @Query("SELECT * from main_weather WHERE latitude = :lat AND longitude = :longitude")
    fun getWeatherEntry(lat: Double, longitude: Double): WeatherEntry

    @Query("SELECT * from main_weather WHERE id = :id")
    fun getWeatherEntry(id: Int): WeatherEntry


    @Query("SELECT * from main_weather WHERE currentLocation = 1")
    fun getWeatherEntryCurrentLocation(): WeatherEntry

    @Query("SELECT * FROM main_weather WHERE currentLocation = 0 LIMIT 1")
    suspend fun getFirstNonCurrentLocationEntry(): WeatherEntry?

    @Delete
    suspend fun deleteWeatherEntry(item: WeatherEntry)

    @Update
    suspend fun updateWeatherEntry(weatherEntry: WeatherEntry)

    @Update
    suspend fun updateForecastEntry(forecast: ForecastEntry)


}