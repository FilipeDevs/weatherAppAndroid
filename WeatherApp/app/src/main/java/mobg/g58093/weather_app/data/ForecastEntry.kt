package mobg.g58093.weather_app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "forecast_weather",
    foreignKeys = [
        ForeignKey(
            entity = WeatherEntry::class,
            parentColumns = ["id"], // Use the primary key of WeatherEntry
            childColumns = ["weatherEntryId"], // Reference WeatherEntry by id
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ForecastEntry(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val locationName : String,

    // fk to weather entry
    val weatherEntryId: Int, // Foreign key referencing WeatherEntry

    val date : String,

    var tempMax : Int,

    var tempMin : Int,

    val humidity: Int,

    val icon : String
    )