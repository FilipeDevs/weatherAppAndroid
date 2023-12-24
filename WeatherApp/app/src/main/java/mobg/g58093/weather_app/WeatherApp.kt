package mobg.g58093.weather_app

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import mobg.g58093.weather_app.ui.navigation.AppNavHost

@Composable
fun WeatherApp(navController: NavHostController = rememberNavController()) {
    AppNavHost(navController = navController)
}