package mobg.g58093.weather_app.ui.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mobg.g58093.weather_app.ui.common.TopAppBar
import mobg.g58093.weather_app.ui.navigation.NavigationDestination

object DetailsDestination : NavigationDestination {
    override val route = "details"
}

@Composable
fun DetailsScreen(
    modifier: Modifier = Modifier,
    //navigateToForecast: () -> Unit,
    navigateToHome: () -> Unit,
)
{
    TopAppBar(navigateToHome = navigateToHome)

}
