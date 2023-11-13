package com.g58093.remise_2.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object About : Screen("about")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen() {
    val navControllerAuth = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomAppBarWithNavigation(navController = navControllerAuth)
        },
    ) { innerPadding ->
        NavHost(navControllerAuth, startDestination = Screen.Home.route) {
            composable(Screen.Home.route) {
                // Apply innerPadding to your content
                HomeScreen(modifier = Modifier.padding(innerPadding))
            }
            composable(Screen.About.route) {
                // Apply innerPadding to your content
                AboutScreen(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}




@Composable
fun BottomAppBarWithNavigation(navController: NavHostController) {
    BottomAppBar(
        containerColor = Color.LightGray,
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Adjust the height as needed
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Centered IconButton for Home
                NavigationItem(navController, Screen.Home, Icons.Default.Home, "Home")

                // Centered IconButton for About
                NavigationItem(navController, Screen.About, Icons.Default.Person, "About")
            }
        }
    )
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