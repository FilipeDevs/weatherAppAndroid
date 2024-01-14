package mobg.g58093.weather_app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import mobg.g58093.weather_app.data.WeatherRepository
import mobg.g58093.weather_app.ui.theme.Weather_appTheme
import mobg.g58093.weather_app.util.LocationPermissionsAndGPSRepository

class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WeatherRepository.initDatabase(applicationContext)
        setContent {
            Weather_appTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherApp()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LocationPermissionsAndGPSRepository.refreshChecks(applicationContext)
        Log.d("MainActivity", "Permissions and GPS recheck")
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    Weather_appTheme {
        WeatherApp()
    }
}