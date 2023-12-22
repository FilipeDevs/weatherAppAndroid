package mobg.g58093.weather_app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "weather_entries", indices = [Index(value = ["locationName"], unique = true)])
data class WeatherEntry (

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // unique
    val locationName : String,

    val longitude : Double,

    val latitude : Double,

    val date : Date,

    val mainTemp : Int,

    val highTemp : Int,

    val lowTemp : Int,

    val weatherType : String,

    val sunriseHour : String,

    val sunsetHour : String,

    val precipitation : Double,

    val wind : Double,

    val humidity : Int,

    val visibility : Double,

    val pressure : Int,

    val currentLocation : Boolean
    )