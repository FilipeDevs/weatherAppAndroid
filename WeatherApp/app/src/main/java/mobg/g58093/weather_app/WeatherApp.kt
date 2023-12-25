package mobg.g58093.weather_app

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import mobg.g58093.weather_app.ui.details.DetailsScreen
import mobg.g58093.weather_app.ui.forecast.ForecastScreen
import mobg.g58093.weather_app.ui.home.HomeScreen

enum class WeatherAppScreen(@StringRes val title: Int) {
    Home(title = R.string.home),
    Details(title = R.string.details),
    Forecast(title = R.string.forecast),
    Locations(title = R.string.locations),
    Search(title = R.string.search)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    @StringRes currentScreenTitle: Int,
    canNavigateBack: Boolean,
    //navigateToLocations : () -> Unit,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit,
) {
    TopAppBar(
        title =  { Text(stringResource(currentScreenTitle)) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "navigate back"
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { /** navigateToLocations */ }) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = null)
            }
        }

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = WeatherAppScreen.valueOf(
        backStackEntry?.destination?.route ?: WeatherAppScreen.Home.name
    )

    Scaffold(
        topBar = {
            TopAppBar(
                currentScreenTitle = currentScreen.title,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                //navigateToLocations = {navController.navigate(WeatherAppScreen.Locations.name)}
            )
        }
    ) {
        innerPadding ->
        NavHost(navController = navController, startDestination = WeatherAppScreen.Home.name) {
            // Home
            composable(route = WeatherAppScreen.Home.name) {
                HomeScreen(navigateToDetails = {navController.navigate(WeatherAppScreen.Details.name)},
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding))
            }

            // Details
            composable(route = WeatherAppScreen.Details.name) {
                DetailsScreen(navigateToForecast = {navController.navigate(WeatherAppScreen.Forecast.name)},
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding))
            }
            // Forecast
            composable(route = WeatherAppScreen.Forecast.name) {
                ForecastScreen( modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding))
            }


        }
    }
}