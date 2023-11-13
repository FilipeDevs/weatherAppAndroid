package com.g58093.remise_2

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
import com.g58093.remise_2.ui.App
import com.g58093.remise_2.ui.theme.Remise_2Theme



class MainActivity : ComponentActivity() {
    enum class AppScreen(@StringRes val title: Int) {
        Login(title = R.string.login),
        Home(title = R.string.home),
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            Remise_2Theme {
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
fun AppPreview() {
    Remise_2Theme {
        App()
    }
}