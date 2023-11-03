package g58093.remise_1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import g58093.remise_1.ui.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    navigateToHome: () -> Unit,
) {

    val loginState by loginViewModel.uiState.collectAsState()

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            TextField(
                value = loginViewModel.userEmail,
                onValueChange = { loginViewModel.updateUserEmail(it) },
                keyboardActions = KeyboardActions( // Enter
                    onDone = { loginViewModel.isEmailValid() }
                ),
                singleLine = true,
                isError = loginState.isEmailWrong
            )

            if (loginState.isEmailWrong) {
                Spacer(modifier = Modifier.height(4.dp)) // Add spacing
                Text(
                    text = "Error, the email is not valid",
                    color = Color.Red
                )
            }
            // When loading the Screen for the 1st time the email is "valid" so it does not show
            // the error message, but it still can't navigate to Home Screen
            else if(loginState.canNavigate) {
                navigateToHome() // Email is valid go to Home Screen
            }
        }

        Button(
            onClick = { loginViewModel.isEmailValid() },
            modifier = Modifier.padding(3.dp)
        ) {
            Text(
                text = "OK",
                color = Color.White
            )
        }
    }
}

