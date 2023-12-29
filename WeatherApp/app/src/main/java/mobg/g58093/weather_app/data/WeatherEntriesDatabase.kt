package mobg.g58093.weather_app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database class with a singleton Instance object.
 */
@Database(entities = [WeatherEntry::class, ForecastEntry::class], version = 1, exportSchema = false)
abstract class WeatherEntriesDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

    companion object {

        private const val DATABASE_NAME = "weather_entries_database"
        private var sInstance: WeatherEntriesDatabase? = null

        fun getInstance(context: Context): WeatherEntriesDatabase {
            if (sInstance == null) {

                val dbBuilder = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherEntriesDatabase::class.java,
                    DATABASE_NAME
                )
                sInstance = dbBuilder.build()

            }
            return sInstance!!
        }
    }
}
