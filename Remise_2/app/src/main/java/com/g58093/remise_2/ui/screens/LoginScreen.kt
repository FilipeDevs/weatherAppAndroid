package com.g58093.remise_2.ui.screens

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.g58093.remise_2.ui.LoginViewModel
import com.g58093.remise_2.R
import com.g58093.remise_2.ui.App
import com.g58093.remise_2.ui.theme.Remise_2Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel(),
    navigateToHome: () -> Unit,
) {

    val loginState by loginViewModel.uiState.collectAsState()

    LaunchedEffect(loginState.authenticated) {
        if (loginState.authenticated) {
            navigateToHome()
        }
    }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            TextField(
                value = loginViewModel.userEmail,
                onValueChange = { loginViewModel.updateUserEmail(it) },
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                value = loginViewModel.userPassword,
                onValueChange = { loginViewModel.updateUserPassword(it) },
                singleLine = true,
            )

            if (loginState.isEmailWrong) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.error),
                    color = Color.Red
                )
            } else if(!loginState.isCredentialsCorrect) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.errorAuth),
                    color = Color.Red
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    loginViewModel.submitCredentials()
                    loginViewModel.isEmailValid()
                },
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.ok),
                    color = Color.White
                )
            }
        }
}





