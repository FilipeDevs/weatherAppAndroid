package g58093.remise_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import g58093.remise_1.ui.App
import g58093.remise_1.ui.AppScreen
import g58093.remise_1.ui.screens.HomeScreen
import g58093.remise_1.ui.screens.LoginScreen
import g58093.remise_1.ui.theme.Remise_1Theme


class MainActivity : ComponentActivity() {
    enum class AppScreen(@StringRes val title: Int) {
        Login(title = R.string.login),
        Home(title = R.string.home),
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            Remise_1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Remise_1Theme {
        App()
    }
}