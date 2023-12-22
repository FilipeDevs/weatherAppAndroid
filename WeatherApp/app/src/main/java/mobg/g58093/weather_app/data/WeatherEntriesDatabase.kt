package mobg.g58093.weather_app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database class with a singleton Instance object.
 */
@Database(entities = [WeatherEntry::class], version = 1, exportSchema = false)
abstract class WeatherEntriesDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile
        private var Instance: WeatherEntriesDatabase? = null

        fun getDatabase(context: Context): WeatherEntriesDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, WeatherEntriesDatabase::class.java, "weather_entries_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
