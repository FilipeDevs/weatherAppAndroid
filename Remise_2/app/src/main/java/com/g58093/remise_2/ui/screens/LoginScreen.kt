package com.g58093.remise_2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.g58093.remise_2.ui.LoginViewModel
import com.g58093.remise_2.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel(),
    navigateToHome: () -> Unit,
) {

    val loginState by loginViewModel.uiState.collectAsState()

    LaunchedEffect(loginState.isEmailWrong) {
        if (loginState.isEmailWrong == false) {
            navigateToHome()
        }
    }

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
                keyboardActions = KeyboardActions( // Enter ?
                    onDone = { loginViewModel.isEmailValid() }
                ),
                singleLine = true,
            )

            if (loginState.isEmailWrong == true) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.error),
                    color = Color.Red
                )
            }
        }

        Button(
            onClick = {
                loginViewModel.isEmailValid()
            },
            modifier = Modifier.padding(3.dp)
        ) {
            Text(
                text = stringResource(R.string.ok),
                color = Color.White
            )
        }
    }
}

