package g58093.remise_1.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
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
        val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val isEmailCorrect = userEmail.matches(emailRegex.toRegex())

        // Email format is wrong
        if (!isEmailCorrect) {
            _uiState.update { currentState -> currentState.copy(isEmailWrong = true) }
        } else {
            // Email format is OK
            _uiState.update { currentState -> currentState.copy(isEmailWrong = false) }
        }

        _uiState.update { currentState -> currentState.copy(canNavigate = true) }
    }
}