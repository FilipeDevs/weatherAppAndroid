package com.g58093.remise_2.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g58093.remise_2.network.AuthApi
import com.g58093.remise_2.network.AuthRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState : StateFlow<LoginState> = _uiState.asStateFlow()

    var userEmail by mutableStateOf("")
        private set

    var userPassword by mutableStateOf("")
        private set

    fun updateUserEmail(userEmail : String) {
        this.userEmail = userEmail
    }

    fun updateUserPassword(userPassword : String) {
        this.userPassword = userPassword
    }

    fun isEmailValid() : Boolean {
        val isEmailCorrect = android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()
        _uiState.update { currentState -> currentState.copy(isEmailWrong = !isEmailCorrect) }
        return isEmailCorrect
    }

    fun submitCredentials() {
        if(isEmailValid()) { // only make api call when email is valid
            val authRequest = AuthRequest(this.userEmail, this.userPassword)
            Log.d("Auth", authRequest.toString())
            viewModelScope.launch {
                try {
                    val call = AuthApi.authApiService.authenticate(authRequest)

                    call.enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                val statusCode = response.code()
                                _uiState.update { currentState -> currentState.copy(authenticated = true) }
                            } else {
                                val errorBody = response.errorBody()?.string()
                                Log.e("Failed", "Error Body: $errorBody")
                                _uiState.update { currentState -> currentState.copy(isCredentialsCorrect = false) }
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.e("Failed", "Throwable: ${t.message}")
                            _uiState.update { currentState -> currentState.copy(isCredentialsCorrect = false) }
                        }
                    })
                } catch (t: Throwable) {
                    Log.e("Failed", "Throwable: ${t.message}")
                    _uiState.update { currentState -> currentState.copy(isCredentialsCorrect = false) }
                } }
        }
    }

}