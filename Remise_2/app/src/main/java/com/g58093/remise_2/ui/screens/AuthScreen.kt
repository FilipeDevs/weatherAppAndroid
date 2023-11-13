package com.g58093.remise_2.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object About : Screen("about")
}

@Composable
fun AuthScreen() {
    val navControllerAuth = rememberNavController()
    NavHost(navControllerAuth, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen() }
        composable(Screen.About.route) { AboutScreen() }
    }
    
    BottomAppBarWithNavigation(navController = navControllerAuth)

}

@Composable
fun BottomAppBarWithNavigation(navController: NavHostController) {
    BottomAppBar(
        containerColor = Color.Magenta
    ) {
        NavigationItem(navController, Screen.Home, Icons.Default.Home, "Home")
        NavigationItem(navController, Screen.About, Icons.Default.Person, "About")
    }
}

@Composable
fun NavigationItem(
    navController: NavHostController,
    screen: Screen,
    icon: ImageVector,
    label: String
) {
    IconButton(
        onClick = { navController.navigate(screen.route) }
    ) {
        Icon(imageVector = icon, contentDescription = label)
    }
}