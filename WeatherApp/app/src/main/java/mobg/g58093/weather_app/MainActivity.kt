package mobg.g58093.weather_app

import android.os.Bundle
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
        // Perform necessary checks for permissions and GPS when the user resumes the app.
        // This is crucial as the user may have exited the app to adjust permissions or GPS settings,
        // and the application needs to ensure it is up-to-date when the user returns.
        LocationPermissionsAndGPSRepository.refreshChecks(applicationContext)
    }

}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    Weather_appTheme {
        WeatherApp()
    }
}