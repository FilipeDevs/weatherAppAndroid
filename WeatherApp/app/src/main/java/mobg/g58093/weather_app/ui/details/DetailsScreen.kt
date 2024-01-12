package mobg.g58093.weather_app.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobg.g58093.weather_app.R
import mobg.g58093.weather_app.ui.home.WeatherApiState
import mobg.g58093.weather_app.ui.home.WeatherViewModel
import mobg.g58093.weather_app.ui.theme.Weather_appTheme

@Composable
fun DetailsScreen(
    modifier: Modifier = Modifier,
    navigateToForecast: () -> Unit,
    weatherViewModel: WeatherViewModel,
) {
    val weatherState by weatherViewModel.weatherState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val currentState = weatherState) {
            is WeatherApiState.Loading -> {
                Text(text = stringResource(R.string.loading))
            }

            is WeatherApiState.Success -> {
                Text(
                    text = stringResource(R.string.details),
                    style = TextStyle(
                        fontSize = 36.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFFFFFFF),
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                // Wind
                Text(
                    text = stringResource(R.string.wind),
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                )
                Text(
                    text = "${currentState.data.wind}km/h",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                // Humidity
                Text(
                    text = stringResource(R.string.humidity),
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                )
                Text(
                    text = "${currentState.data.humidity}%",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                // Visibility (in meters)
                Text(
                    text = stringResource(R.string.visibility),
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                )
                Text(
                    text = "${currentState.data.visibility}m",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                // Pressure (hPa)
                Text(
                    text = stringResource(R.string.pressure),
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                )
                Text(
                    text = "${currentState.data.pressure}hPa",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
                Spacer(modifier = Modifier.height(50.dp))
                // Navigate to Forecast Screen
                Button(
                    onClick = navigateToForecast,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.forecast),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight(400),
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                    )
                }
            }

            else -> {}
        }

    }
}




