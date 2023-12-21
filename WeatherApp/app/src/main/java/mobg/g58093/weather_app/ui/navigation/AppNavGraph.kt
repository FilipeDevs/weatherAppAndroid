package mobg.g58093.weather_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import mobg.g58093.weather_app.ui.details.DetailsDestination
import mobg.g58093.weather_app.ui.details.DetailsScreen
import mobg.g58093.weather_app.ui.home.HomeDestination
import mobg.g58093.weather_app.ui.home.HomeScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    ) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToHome = {navController.navigate(HomeDestination.route)},
                navigateToDetails = {navController.navigate(DetailsDestination.route)}
            )
        }
        composable(route = DetailsDestination.route) {
            DetailsScreen(
                navigateToHome = {navController.navigate(HomeDestination.route)}
            )
        }

    }

}