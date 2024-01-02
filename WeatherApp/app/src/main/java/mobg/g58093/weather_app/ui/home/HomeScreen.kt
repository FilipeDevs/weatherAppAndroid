package mobg.g58093.weather_app.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import mobg.g58093.weather_app.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    weatherViewModel: WeatherViewModel,
    navigateToDetails: () -> Unit,
    )
{
        val weatherState by weatherViewModel.weatherState.collectAsState() // Main weather state
        val permissionState by weatherViewModel.requestLocationPermission.collectAsState() // Current app perms state
        val selectedLocation by weatherViewModel.selectedLocation.collectAsState() // Selected location state
        var permission by remember { mutableStateOf(true) } // rationale state
        val context = LocalContext.current

        // Create a permission launcher
        val requestPermissionLauncher =
            rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted: Boolean ->
                if (isGranted) {
                    weatherViewModel.updatePermissions(true)
                    permission = true
                    // permissions granted so fetch current location
                    weatherViewModel.fetchWeatherCurrentLocation()
                } else {
                    weatherViewModel.updatePermissions(false)
                    permission = false
                }
            })

        if (!permission) {
            ShowLocationPermissionPopup(context)
        }

        LaunchedEffect(selectedLocation.currentLocation) {
            // Only request permissions if the selected location is current location
            if (!permissionState) {
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center, // Center vertically
            horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
        ) {
            when(val currentState = weatherState) {
                is WeatherApiState.Loading -> {
                    Text("Loading...")
                }
                is WeatherApiState.Success -> {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = currentState.data.locationName
                                + " - " + currentState.data.country,
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight(400),
                            color  = Color(0xFF616161)
                        )
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    // Date
                    Text(
                        text = convertCurrentDateToFormattedDate(),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight(400),
                            color  = Color(0xFF616161)
                        )
                    )
                    // Current temperature
                    Text(
                        text = currentState.data.mainTemp.toString() + "°C",
                        style = TextStyle(
                            fontSize = 80.sp,
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
                                    text = currentState.data.lowTemp.toString() + "°C",
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight(400),
                                        color = Color(0xFF616161),
                                    )
                                )
                            }

                        }
                        Spacer(modifier = Modifier.width(10.dp))
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
                                    text = currentState.data.highTemp.toString() + "°C",
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
                            .width(190.dp)
                            .height(180.dp),
                        model = "https://openweathermap.org/img/wn/${(weatherState as WeatherApiState.Success).data.weatherIcon}@2x.png",
                        placeholder = painterResource(id = R.drawable.deviconweather),
                        contentDescription = "The delasign logo",
                    )

                    Text(
                        text = currentState.data.weatherType,
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
                                    text = currentState.data.sunriseHour,
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
                                    text = currentState.data.sunsetHour,
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight(400),
                                        color = Color(0xFF616161),
                                    )
                                )
                            }

                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = navigateToDetails,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "Details",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight(400),
                                color = Color(0xFF616161),
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Last updated : " + currentState.data.date,
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight(400),
                                color = Color(0xFF616161),
                            )
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Button(
                            onClick = { weatherViewModel.refreshData() },
                            modifier = Modifier.padding(8.dp)

                        ) {
                            Text(
                                text = "Refresh",
                                color = Color.White
                            )
                        }
                    }

                }
                else -> {
                    // Show error state
                    val errorMessage = (weatherState as WeatherApiState.Error).message
                    Text("Error: $errorMessage")
                }
            }

        }
}

@Composable
fun ShowLocationPermissionPopup(context: Context) {
    // State to track whether the dialog is shown
    var isDialogVisible by remember { mutableStateOf(true) }

    if (isDialogVisible) {
        AlertDialog(
            onDismissRequest = {
                isDialogVisible = false
            },
            title = { Text("Location Permission Required") },
            text = {
                Column {
                    Text("The app requires access to your location for a better experience.")
                    Spacer(modifier = Modifier.height(8.dp))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri: Uri = Uri.fromParts("package", context.packageName, null)
                        intent.data = uri
                        context.startActivity(intent)
                        isDialogVisible = false
                    }
                ) {
                    Text("Go to Settings")
                }
            },
            dismissButton = {
                Button(onClick = {
                    isDialogVisible = false
                }) {
                    Text("Dismiss")
                }
            }
        )
    }
}


fun convertCurrentDateToFormattedDate(): String {
    val date = Date()
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return sdf.format(date)
}

