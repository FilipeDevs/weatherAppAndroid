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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import mobg.g58093.weather_app.R
import mobg.g58093.weather_app.network.WeatherResponse
import mobg.g58093.weather_app.ui.common.TopAppBar
import mobg.g58093.weather_app.ui.navigation.NavigationDestination
import mobg.g58093.weather_app.ui.theme.Weather_appTheme

object HomeDestination : NavigationDestination {
    override val route = "home"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel(),
    navigateToDetails: () -> Unit,
    navigateToHome: () -> Unit,
    //navigateToLocations : () -> Unit,
    )
{
    val weatherState by homeViewModel.weatherState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(navigateToHome = navigateToHome)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
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
                        text = "Monday, 13 Nov 2023",
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
                                    text = "5°C",
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
                                    text = "13°C",
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
                        modifier = Modifier.width(200.dp).height(200.dp),
                        model = "https://openweathermap.org/img/wn/11d@2x.png",
                        placeholder = painterResource(id = R.drawable.deviconweather),
                        contentDescription = "The delasign logo",
                    )

                    Text(
                        text = "Heavy Rain",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight(400),
                            color = Color(0xFF616161),
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
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
                                    text = "09:18 AM",
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight(400),
                                        color = Color(0xFF616161),
                                    )
                                )
                            }

                        }
                        Spacer(modifier = Modifier.width(24.dp))
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
                                    text = "06:32 PM",
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight(400),
                                        color = Color(0xFF616161),
                                    )
                                )
                            }

                        }
                    }
                    Spacer(modifier = Modifier.height(80.dp))
                    Text(
                        text = "Details",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight(400),
                            color = Color(0xFF616161),
                        )
                    )
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
}
