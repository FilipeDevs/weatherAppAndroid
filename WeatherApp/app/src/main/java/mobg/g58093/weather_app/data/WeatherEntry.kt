package mobg.g58093.weather_app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "main_weather",
    indices = [Index(value = ["longitude", "latitude"], unique = true)]
)
data class WeatherEntry(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val locationName: String,

    val country: String,

    // Unique pair
    val longitude: Double,
    val latitude: Double,

    val date: String,

    val mainTemp: Int,

    val highTemp: Int,

    val lowTemp: Int,

    val weatherType: String,

    val weatherIcon: String,

    val sunriseHour: String,

    val sunsetHour: String,

    val wind: Double,

    val humidity: Int,

    val visibility: Int,

    val pressure: Int,

    val currentLocation: Boolean
)


