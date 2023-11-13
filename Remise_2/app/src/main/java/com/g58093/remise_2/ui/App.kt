package com.g58093.remise_2.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.g58093.remise_2.R
import com.g58093.remise_2.ui.screens.AuthScreen
import com.g58093.remise_2.ui.screens.LoginScreen

enum class AppScreen(@StringRes val title: Int) {
    Login(title = R.string.login),
    Auth(title = R.string.auth)
}

@Composable
fun App() {
    val navController = rememberNavController()

    // Navigation Controller (not auth)
    NavHost(
        navController = navController,
        startDestination = AppScreen.Login.name,
    ) {
        composable(route = AppScreen.Login.name) {// Login Screen
            LoginScreen(navigateToHome = {
                navController.navigate(AppScreen.Auth.name)
            })
        }
        composable(route = AppScreen.Auth.name) {// Auth Screen
            AuthScreen()
        }
    }
}