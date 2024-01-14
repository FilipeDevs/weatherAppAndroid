package mobg.g58093.weather_app.ui.home

import android.content.Context
import android.provider.Settings
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mobg.g58093.weather_app.R
import mobg.g58093.weather_app.util.LocationPermissionsAndGPSRepository
import mobg.g58093.weather_app.util.SelectedLocationRepository
import mobg.g58093.weather_app.util.SelectedLocationState
import mobg.g58093.weather_app.util.checkIsGPSEnabled
import mobg.g58093.weather_app.util.getCountryFromCode
import mobg.g58093.weather_app.util.getDynamicResourceId
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    weatherViewModel: WeatherViewModel,
    navigateToDetails: () -> Unit,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    val context = LocalContext.current

    val weatherState by weatherViewModel.weatherState.collectAsState() // Main weather state
    val selectedLocation by SelectedLocationRepository.selectedLocationState.collectAsState() // Selected location state

    val permissionState by LocationPermissionsAndGPSRepository.permissions.collectAsState() // Permissions state
    val gpsState by LocationPermissionsAndGPSRepository.gps.collectAsState() // Gps

    val firstLaunchPerms by weatherViewModel.firstLaunchPerms.collectAsState()

    var showRationalte by remember { mutableStateOf(false) } // rationale state

    // Permission launcher
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted: Boolean ->
                if (isGranted) {
                    weatherViewModel.updatePermissions(true)
                    // permissions granted so fetch current location
                    weatherViewModel.fetchWeatherCurrentLocation()
                    weatherViewModel.updateFirstLaunchPermissions()
                } else {
                    weatherViewModel.updatePermissions(false)
                    showRationalte = true
                }

            })

    LaunchedEffect(Unit) {
        // Show snackbar if gps is not enabled (and app has permissions)
        if (!gpsState && selectedLocation.currentLocation && permissionState) {
            val result = snackbarHostState.showSnackbar(
                message = "Please enable GPS",
                actionLabel = "Enable GPS",
                duration = SnackbarDuration.Short
            )
            when (result) {
                // Redirect user to location settings
                SnackbarResult.ActionPerformed -> {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                }

                SnackbarResult.Dismissed -> {}
            }
        }
        // Request permissions (only once after launch)
        if (!permissionState && selectedLocation.currentLocation && !firstLaunchPerms) {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    if (showRationalte && !firstLaunchPerms) {
        ShowLocationPermissionPopup(context, weatherViewModel)
    }

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
                // Location Name
                Text(
                    text = "${currentState.data.locationName} \n ${getCountryFromCode(currentState.data.country)}",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.tertiary,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(5.dp))
                // Date
                Text(
                    text = convertCurrentDateToFormattedDate(),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                )
                // Current temperature
                Text(
                    text = "${currentState.data.mainTemp}°C",
                    style = TextStyle(
                        fontSize = 80.sp,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
                Spacer(modifier = Modifier.height(5.dp))
                // Highest and Lowest Temperatures
                Row {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(17.dp),
                                painter = painterResource(id = R.drawable.downarrow),
                                contentDescription = "low temp",
                                contentScale = ContentScale.None
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${currentState.data.lowTemp}°C",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight(400),
                                    color = MaterialTheme.colorScheme.tertiary
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
                                contentDescription = "high temp",
                                contentScale = ContentScale.None
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${currentState.data.highTemp}°C",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight(400),
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            )
                        }

                    }
                }
                // Weather Icon
                Image(
                    modifier = Modifier
                        .width(190.dp)
                        .height(180.dp),
                    painter = painterResource(id = getDynamicResourceId(currentState.data.weatherIcon)),
                    contentDescription = "weather icon",
                )

                Text(
                    text = currentState.data.weatherType,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                )
                Spacer(modifier = Modifier.height(15.dp))
                // Sunrise and Sunset
                Row {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                modifier = Modifier
                                    .width(19.dp)
                                    .height(17.dp),
                                painter = painterResource(id = R.drawable.sunrisevector),
                                contentDescription = "sunrise icon",
                                contentScale = ContentScale.None
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = currentState.data.sunriseHour,
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight(400),
                                    color = MaterialTheme.colorScheme.tertiary
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
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            )
                        }

                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                // Navigate to Weather details
                Button(
                    onClick = navigateToDetails,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.details),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight(400),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                // Status of data
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text( // When was the current weather being presented updated
                        text = stringResource(R.string.lastUpdated) + currentState.data.date,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight(400),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    // Refresh weather data
                    Button(
                        onClick = {
                            weatherViewModel.refreshData()
                            showGpsSnackbar(context, selectedLocation, snackbarHostState, scope)
                        },
                        modifier = Modifier.padding(8.dp)

                    ) {
                        Text(
                            text = stringResource(R.string.refresh),
                            color = Color.White
                        )
                    }
                }
            }

            else -> {
                // Show error state
                val errorMessage = (weatherState as WeatherApiState.Error).message
                Text(errorMessage)
            }
        }

    }
}

fun showGpsSnackbar(
    context: Context, selectedLocation: SelectedLocationState,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    scope.launch {
        if (!checkIsGPSEnabled(context) && selectedLocation.currentLocation) {
            val result = snackbarHostState.showSnackbar(
                message = "Please enable GPS",
                actionLabel = "Enable GPS",
                duration = SnackbarDuration.Short
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                }

                SnackbarResult.Dismissed -> {
                }
            }
        }
    }
}

@Composable
fun ShowLocationPermissionPopup(context: Context, weatherViewModel: WeatherViewModel) {
    // State to track whether the dialog is shown
    var isDialogVisible by remember { mutableStateOf(true) }

    if (isDialogVisible) {
        AlertDialog(
            onDismissRequest = {
                weatherViewModel.updateFirstLaunchPermissions()
                isDialogVisible = false
            },
            title = { Text(text = stringResource(R.string.locationRequired)) },
            text = {
                Column {
                    Text(text = stringResource(R.string.rationaleLocation))
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
                        weatherViewModel.updateFirstLaunchPermissions()
                        isDialogVisible = false
                    }
                ) {
                    Text(
                        text = stringResource(R.string.settings), style = TextStyle(
                            fontWeight = FontWeight(400),
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    )
                }
            },
            dismissButton = {
                Button(onClick = {
                    weatherViewModel.updateFirstLaunchPermissions()
                    isDialogVisible = false
                }) {
                    Text(
                        text = stringResource(R.string.dismiss), style = TextStyle(
                            fontWeight = FontWeight(400),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }
        )
    }
}


fun convertCurrentDateToFormattedDate(): String {
    val date = Date()
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.US)
    return sdf.format(date)
}

