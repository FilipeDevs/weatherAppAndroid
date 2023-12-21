package mobg.g58093.weather_app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobg.g58093.weather_app.ui.common.TopAppBar
import mobg.g58093.weather_app.ui.navigation.NavigationDestination
import mobg.g58093.weather_app.ui.theme.Weather_appTheme

object HomeDestination : NavigationDestination {
    override val route = "home"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToDetails: () -> Unit,
    navigateToHome: () -> Unit,
    //navigateToLocations : () -> Unit,
    )
{
    Scaffold(
        topBar = {
            TopAppBar(navigateToHome = navigateToHome)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center, // Center vertically
            horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
        ) {
            Text(

                text = "Monday, 13 Nov 2023",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight(400),
                    color  = Color(0xFF616161)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "7°C",
                style = TextStyle(
                    fontSize = 96.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFFFFF),
                    )
            )
        }
    }





}
