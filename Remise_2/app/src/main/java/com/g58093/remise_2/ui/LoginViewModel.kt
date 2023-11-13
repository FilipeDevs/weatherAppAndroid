package com.g58093.remise_2.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.g58093.remise_2.ui.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState : StateFlow<LoginState> = _uiState.asStateFlow()

    var userEmail by mutableStateOf("")
        private set

    fun updateUserEmail(userEmail : String) {
        this.userEmail = userEmail
    }

    fun isEmailValid() {
        val isEmailCorrect = android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()

        _uiState.update { currentState -> currentState.copy(isEmailWrong = !isEmailCorrect) }

    }
}