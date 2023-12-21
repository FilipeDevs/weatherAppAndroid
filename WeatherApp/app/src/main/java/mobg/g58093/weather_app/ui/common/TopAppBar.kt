package mobg.g58093.weather_app.ui.common


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    navigateToHome: () -> Unit,
    //navigateToLocations : () -> Unit,
    ) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment =  Alignment.CenterVertically
            ) {
                Text(
                    text = "Brussels",
                    modifier = Modifier.weight(1f).clickable { navigateToHome() }
                )
            }
        },
        actions = {
            IconButton(onClick = { /*navigateToLocations()*/ }) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = null)
            }
        }
    )
}
