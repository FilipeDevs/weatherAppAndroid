package mobg.g58093.weather_app.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.Flow


object WeatherRepository {
    private var database : WeatherEntriesDatabase? = null

    fun initDatabase(context: Context){
        if (database == null) {
            database = WeatherEntriesDatabase.getInstance(context)
        }
    }

    suspend fun insertWeatherEntry(weatherEntry: WeatherEntry){
        database?.weatherDao()?.insertWeatherEntry(weatherEntry)
    }

    suspend fun insertForecastEntry(forecast: ForecastEntry){
        database?.weatherDao()?.insertForecastEntry(forecast)
    }

    suspend fun getAllWeatherEntries() : List<WeatherEntry> {
        database?.let { theDatabase ->
            return theDatabase.weatherDao().getAllWeatherEntries()
        }
        return listOf()
    }

    suspend fun getAllForecastsByLocation(id: Int) : List<ForecastEntry> {
        database?.let { theDatabase ->
            return theDatabase.weatherDao().getAllForecastEntriesByWeather(id)
        }
        return listOf()
    }
    suspend fun getWeatherEntry(lat: Double, long : Double): WeatherEntry? {
        database?.let { theDatabase ->
            return theDatabase.weatherDao().getWeatherEntry(lat, long)
        }
        return null
    }

    suspend fun getWeatherEntry(id : Int): WeatherEntry? {
        database?.let { theDatabase ->
            return theDatabase.weatherDao().getWeatherEntry(id)
        }
        return null
    }

    suspend fun getFirstNonCurrentLocationEntry(): WeatherEntry? {
        database?.let { theDatabase ->
            return theDatabase.weatherDao().getFirstNonCurrentLocationEntry()
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

    suspend fun updateForecastEntry(forecast: ForecastEntry) {
        database?.weatherDao()?.updateForecastEntry(forecast)
    }

}
