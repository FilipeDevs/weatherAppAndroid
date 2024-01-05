package mobg.g58093.weather_app.ui.locations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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


@Composable
fun LocationsScreen(
    modifier: Modifier = Modifier,
    locationsViewModel: LocationsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToSearch : () -> Unit,
) {
    val locationsState by locationsViewModel.locationsState.collectAsState()
    val selectedLocation by locationsViewModel.selectedLocation.collectAsState()

    LaunchedEffect(locationsViewModel.getAllUserLocations()) {}

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // Column for the main content (including the LocationList)
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LocationList(locationsList = locationsState.locationsList, modifier = modifier,
                selectedLocation, locationsViewModel)
        }

        // Floating "+" button
        FloatingActionButton(
            onClick = {
                navigateToSearch()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
        }
    }
}

@Composable
fun LocationList(locationsList : List<WeatherEntry>, modifier: Modifier,
                 selectedLocation : SelectedLocationState, locationsViewModel: LocationsViewModel) {
    LazyColumn(modifier = modifier) {
        items(items = locationsList, key = { it.id }) { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(
                        text = item.locationName,
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight(400),
                            color = Color(0xFFFFFFFF),
                        )
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = item.mainTemp.toString() + "°C",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight(400),
                            color = Color(0xFF616161),
                            )
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = item.weatherType,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight(400),
                            color = Color(0xFF616161),
                        )
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
                // Weather Icon
                AsyncImage(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp),
                    model = "https://openweathermap.org/img/wn/${item.weatherIcon}@2x.png",
                    placeholder = painterResource(id = R.drawable.deviconweather),
                    contentDescription = "The delasign logo",
                )
                Checkbox(
                    checked = selectedLocation.locationName == item.locationName
                        && selectedLocation.countryCode == item.country,
                    onCheckedChange = { locationsViewModel.changeSelectedLocation(item.id) })
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