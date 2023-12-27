package mobg.g58093.weather_app.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import mobg.g58093.weather_app.AppViewModelProvider
import mobg.g58093.weather_app.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToDetails: () -> Unit,
    )
{
        val weatherState by homeViewModel.weatherState.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center, // Center vertically
            horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
        ) {
            when(weatherState) {
                is WeatherApiState.Loading -> {
                    Text("Loading...")
                }
                is WeatherApiState.Success -> {
                    // Date
                    Text(
                        text = convertCurrentDateToFormattedDate(),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight(400),
                            color  = Color(0xFF616161)
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    // Current temperature
                    Text(
                        text = (weatherState as WeatherApiState.Success).data.mainTemp.toString() + "°C",
                        style = TextStyle(
                            fontSize = 96.sp,
                            fontWeight = FontWeight(400),
                            color = Color(0xFFFFFFFF),
                        )
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    // Highest and Lowest Temperatures
                    Row(
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    modifier = Modifier
                                        .width(12.dp)
                                        .height(17.dp),
                                    painter = painterResource(id = R.drawable.downarrow),
                                    contentDescription = "image description",
                                    contentScale = ContentScale.None
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = (weatherState as WeatherApiState.Success).data.lowTemp.toString() + "°C",
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight(400),
                                        color = Color(0xFF616161),
                                    )
                                )
                            }

                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    modifier = Modifier
                                        .width(12.dp)
                                        .height(17.dp),
                                    painter = painterResource(id = R.drawable.uparrow),
                                    contentDescription = "image description",
                                    contentScale = ContentScale.None
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = (weatherState as WeatherApiState.Success).data.highTemp.toString() + "°C",
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight(400),
                                        color = Color(0xFF616161),
                                    )
                                )
                            }

                        }
                    }
                    // Weather Icon
                    AsyncImage(
                        modifier = Modifier
                            .width(230.dp)
                            .height(230.dp),
                        model = "https://openweathermap.org/img/wn/${(weatherState as WeatherApiState.Success).data.weatherIcon}@2x.png",
                        placeholder = painterResource(id = R.drawable.deviconweather),
                        contentDescription = "The delasign logo",
                    )

                    Text(
                        text = (weatherState as WeatherApiState.Success).data.weatherType,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight(400),
                            color = Color(0xFF616161),
                        )
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    // Sunrise and Sunset
                    Row(
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    modifier = Modifier
                                        .width(19.dp)
                                        .height(17.dp),
                                    painter = painterResource(id = R.drawable.sunrisevector),
                                    contentDescription = "image description",
                                    contentScale = ContentScale.None
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = (weatherState as WeatherApiState.Success).data.sunriseHour,
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight(400),
                                        color = Color(0xFF616161),
                                    )
                                )
                            }

                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    modifier = Modifier
                                        .width(19.dp)
                                        .height(17.dp),
                                    painter = painterResource(id = R.drawable.sunsetvector),
                                    contentDescription = "image description",
                                    contentScale = ContentScale.None
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = (weatherState as WeatherApiState.Success).data.sunsetHour,
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight(400),
                                        color = Color(0xFF616161),
                                    )
                                )
                            }

                        }
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                    Button(
                        onClick = navigateToDetails,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "Details",
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight(400),
                                color = Color(0xFF616161),
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Last updated : " + (weatherState as WeatherApiState.Success).data.date,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight(400),
                            color = Color(0xFF616161),
                        )
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Button(
                        onClick = { homeViewModel.refreshData() },
                        modifier = Modifier.padding(8.dp)

                    ) {
                        Text(
                            text = "Refresh",
                            color = Color.White
                        )
                    }
                }
                is WeatherApiState.Error -> {
                    // Show error state
                    val errorMessage = (weatherState as WeatherApiState.Error).message
                    Text("Error: $errorMessage")
                }

                else -> {}
            }

        }
}


fun convertCurrentDateToFormattedDate(): String {
    val date = Date()
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return sdf.format(date)
}

