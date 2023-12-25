package mobg.g58093.weather_app.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobg.g58093.weather_app.ui.theme.Weather_appTheme

@Composable
fun DetailsScreen(
    modifier: Modifier = Modifier,
    navigateToForecast: () -> Unit,
)
{
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center, // Center vertically
            horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
        ) {
            Text(
                text = "Details",
                style = TextStyle(
                    fontSize = 36.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFFFFF),
                    )
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Precipitation",
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF616161),

                    )
            )
            Text(
                text = "30.0 mm",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFFFFF),

                    )
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Wind",
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF616161),

                    )
            )
            Text(
                text = "10.23 km/h",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFFFFF),

                    )
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Humidity",
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF616161),

                    )
            )
            Text(
                text = "56 %",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFFFFF),

                    )
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Visibility",
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF616161),

                    )
            )
            Text(
                text = "14.83 km",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFFFFF),

                    )
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Pressure",
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF616161),

                    )
            )
            Text(
                text = "1012 hPa",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFFFFF),

                    )
            )
            Spacer(modifier = Modifier.height(50.dp))
            Button(
               onClick = navigateToForecast,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Forecast")
            }
        }
}


@Preview(showBackground = true)
@Composable
fun DetailsScreenPreview() {
    Weather_appTheme {

    }

}




