package com.g58093.remise_2.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.g58093.remise_2.R
import com.g58093.remise_2.ui.screens.HomeScreen
import com.g58093.remise_2.ui.screens.LoginScreen

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