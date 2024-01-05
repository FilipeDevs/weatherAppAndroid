package mobg.g58093.weather_app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "forecast_weather",
    foreignKeys = [
        ForeignKey(
            entity = WeatherEntry::class,
            parentColumns = ["id"],
            childColumns = ["weatherEntryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ForecastEntry(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val locationName: String,

    // fk to weather entry
    val weatherEntryId: Int,

    val date: String,

    var tempMax: Int,

    var tempMin: Int,

    val humidity: Int,

    val icon: String
)