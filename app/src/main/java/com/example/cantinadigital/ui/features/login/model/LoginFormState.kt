package com.example.cantinadigital.ui.features.login.model

data class LoginFormState(
    val email: String = "",
    val pass: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)