package com.example.cantinadigital.ui.features.signup.model

data class SignUpFormState(
    val name: String = "",
    val studentClass: ThirdYearClass = ThirdYearClass.Y,
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

enum class ThirdYearClass {
    W, X, Y
}