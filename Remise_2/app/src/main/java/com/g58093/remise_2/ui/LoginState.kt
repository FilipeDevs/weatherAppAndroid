package com.g58093.remise_2.ui

data class LoginState(
    // State can't be true or false by default because either it will show an error
    // message by default or navigate to the next screen by default
    val isEmailWrong : Boolean? = null,
)
