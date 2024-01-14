package mobg.g58093.weather_app.ui.locations

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import mobg.g58093.weather_app.util.AppViewModelProvider
import mobg.g58093.weather_app.R
import mobg.g58093.weather_app.util.SelectedLocationState
import mobg.g58093.weather_app.data.WeatherEntry
import mobg.g58093.weather_app.util.SelectedLocationRepository


@Composable
fun LocationsScreen(
    modifier: Modifier,
    locationsViewModel: LocationsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToSearch: () -> Unit,
) {
    val locationsState by locationsViewModel.locationsState.collectAsState()
    val selectedLocation by SelectedLocationRepository.selectedLocationState.collectAsState() // Selected location state

    LaunchedEffect(locationsViewModel.getAllUserLocations()) {}

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        // Column for the main content (including the LocationList)
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LocationList(
                locationsList = locationsState.locationsList, modifier = modifier,
                selectedLocation, locationsViewModel
            )
        }

        // Floating "+" button
        FloatingActionButton(
            onClick = {
                navigateToSearch()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add new location",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun LocationList(
    locationsList: List<WeatherEntry>,
    modifier: Modifier,
    selectedLocation: SelectedLocationState,
    locationsViewModel: LocationsViewModel
) {
    LazyColumn(modifier = modifier) {
        items(items = locationsList, key = { it.id }) { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Column(modifier = Modifier.clickable {
                    locationsViewModel.changeSelectedLocation(item.id)
                }) {
                    Text(
                        text = item.locationName,
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight(400),
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${item.mainTemp}°C",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight(400),
                                color = MaterialTheme.colorScheme.tertiary,
                            )
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = item.weatherType,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight(400),
                                color = MaterialTheme.colorScheme.tertiary,
                            )
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        // Weather Icon
                        RadioButton(
                            selected = selectedLocation.longitude == item.longitude
                                    && selectedLocation.latitude == item.latitude,
                            onClick = { locationsViewModel.changeSelectedLocation(item.id) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.tertiary,
                            )
                        )
                        IconButton(
                            onClick = { locationsViewModel.deleteWeatherEntry(item.id) },
                            modifier = Modifier
                                .size(35.dp)
                                .padding(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.Red
                            )
                        }
                    }

                }
            }
        }
    }
}