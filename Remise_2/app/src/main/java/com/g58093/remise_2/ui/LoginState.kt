package com.g58093.remise_2.ui

data class LoginState(
    val isEmailWrong : Boolean = false,
    val isCredentialsCorrect : Boolean = true,
    val authenticated : Boolean = false
)
