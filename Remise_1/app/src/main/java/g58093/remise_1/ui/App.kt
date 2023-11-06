package g58093.remise_1.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import g58093.remise_1.R
import g58093.remise_1.ui.screens.HomeScreen
import g58093.remise_1.ui.screens.LoginScreen

enum class AppScreen(@StringRes val title: Int) {
    Login(title = R.string.login),
    Home(title = R.string.home),
}

@Composable
fun App() {
    val navController = rememberNavController()

    // Navigation Controller
    NavHost(
        navController = navController,
        startDestination = AppScreen.Login.name,
    ) {
        composable(route = AppScreen.Login.name) {// Login Screen
            LoginScreen(navigateToHome = {
                    navController.navigate(AppScreen.Home.name)
            })
        }
        composable(route = AppScreen.Home.name) {// Home Screen
            HomeScreen()
        }
    }
}