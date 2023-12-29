package mobg.g58093.weather_app.ui.forecast

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import mobg.g58093.weather_app.AppViewModelProvider
import mobg.g58093.weather_app.R
import mobg.g58093.weather_app.data.ForecastEntry
import mobg.g58093.weather_app.ui.home.WeatherApiState
import mobg.g58093.weather_app.ui.theme.Weather_appTheme

@Composable
fun ForecastScreen(
    modifier: Modifier = Modifier,
    forecastViewModel: ForecastViewModel = viewModel(factory = AppViewModelProvider.Factory),
)
{
        val forecastState by forecastViewModel.forecastState.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start // Center horizontally
        ) {
            Text(
                text = "Forecast",
                style = TextStyle(
                    fontSize = 36.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFFFFF),
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            when(forecastState) {
                is ForecastApiState.Loading -> {
                    Text("Loading...")
                }
                is ForecastApiState.Success -> {
                    ForecastList(
                        forecastList = (forecastState as ForecastApiState.Success).data,
                        modifier = modifier
                    )
                } else -> { // Error
                    Text((forecastState as ForecastApiState.Error).message)
                }
            }

        }

}

@Composable
private fun ForecastList(forecastList : List<ForecastEntry>, modifier: Modifier) {
    LazyColumn(modifier = modifier) {
        items(items = forecastList, key = { it.id }) { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Today",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFFFFFFF),
                    )
                )
                Spacer(modifier = Modifier.width(50.dp))
                Image(
                    painter = painterResource(id = R.drawable.water),
                    contentDescription = "water icon"
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "42%",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF616161),
                    )
                )
                Spacer(modifier = Modifier.width(50.dp))
                // Weather Icon
                AsyncImage(
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp),
                    model = "https://openweathermap.org/img/wn/11d@2x.png",
                    placeholder = painterResource(id = R.drawable.deviconweather),
                    contentDescription = "The delasign logo",
                )
                Text(
                    text = "7°",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF616161),

                        )
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = "10°",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFFFFFFF),
                    )
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun ForecastScreenPreview() {
    Weather_appTheme {
        ForecastScreen(modifier = Modifier)
    }

}




