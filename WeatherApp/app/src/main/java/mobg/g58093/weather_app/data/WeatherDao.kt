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

    @Query("SELECT * from forecast_weather WHERE locationName = :name")
    suspend fun getAllForecastEntriesByLocation(name : String): List<ForecastEntry>

    @Query("SELECT * from main_weather WHERE locationName = :name AND country = :country")
    fun getWeatherEntry(name : String, country : String) : WeatherEntry

    @Query("SELECT * from main_weather WHERE currentLocation = 1")
    fun getWeatherEntryCurrentLocation() : WeatherEntry

    @Query("SELECT * FROM main_weather WHERE currentLocation = 0 LIMIT 1")
    suspend fun getFirstNonCurrentLocationEntry(): WeatherEntry?

    @Delete
    suspend fun deleteWeatherEntry(item: WeatherEntry)

    @Update
    suspend fun updateWeatherEntry(weatherEntry: WeatherEntry)

    @Update
    suspend fun updateForecastEntry(forecast: ForecastEntry)


}