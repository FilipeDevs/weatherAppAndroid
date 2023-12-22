package mobg.g58093.weather_app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWeatherEntry(item: WeatherEntry)

    @Query("SELECT * from weather_entries")
    suspend fun getAllWeatherEntries(): List<WeatherEntry>

    @Query("SELECT * from weather_entries WHERE locationName = :name")
    fun getWeatherEntry(name : String) : WeatherEntry

    @Query("SELECT * from weather_entries WHERE currentLocation = 1")
    fun getWeatherEntryCurrentLocation() : WeatherEntry

    @Delete
    suspend fun deleteWeatherEntry(item: WeatherEntry)


}