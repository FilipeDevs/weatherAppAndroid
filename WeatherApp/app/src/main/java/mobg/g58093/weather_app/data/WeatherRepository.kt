package mobg.g58093.weather_app.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.Flow


object WeatherRepository {
    private var database : WeatherEntriesDatabase? = null

    fun initDatabase(context: Context){
        Log.d("WeatherRepository", "initDatabase")
        Log.d("WeatherRepository", "database ${database}")
        if (database == null) {
            database = WeatherEntriesDatabase.getInstance(context)
        }
    }

    suspend fun insertWeatherEntry(weatherEntry: WeatherEntry){
        database?.weatherDao()?.insertWeatherEntry(weatherEntry)
    }

    suspend fun getAllWeatherEntries() : List<WeatherEntry> {
        database?.let { theDatabase ->
            return theDatabase.weatherDao().getAllWeatherEntries()
        }
        return listOf()
    }

    suspend fun getWeatherEntry(locationName: String): WeatherEntry? {
        database?.let { theDatabase ->
            return theDatabase.weatherDao().getWeatherEntry(locationName)
        }
        return null
    }

    suspend fun getWeatherEntryCurrentLocation(): WeatherEntry? {
        database?.let { theDatabase ->
            return theDatabase.weatherDao().getWeatherEntryCurrentLocation()
        }
        return null
    }

    suspend fun deleteWeatherEntry(weather: WeatherEntry) {
        database?.weatherDao()?.deleteWeatherEntry(weather)
    }

    suspend fun updateWeatherEntry(weather: WeatherEntry) {
        database?.weatherDao()?.updateWeatherEntry(weather)
    }

}
