package mobg.g58093.weather_app.ui.locations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mobg.g58093.weather_app.AppViewModelProvider
import androidx.compose.material3.Text

@Composable
fun LocationsScreen(
    modifier: Modifier = Modifier,
    locationsViewModel: LocationsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    //navigateToSearch : () -> Unit,
) {
    val locationsState by locationsViewModel.locationsState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.Start // Center horizontally
    ) {
        Text("Locations")
    }


}