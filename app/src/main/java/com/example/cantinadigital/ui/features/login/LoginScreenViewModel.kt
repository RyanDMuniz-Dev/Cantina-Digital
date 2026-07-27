package com.example.cantinadigital.ui.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cantinadigital.data.repository.AuthRepository
import com.example.cantinadigital.ui.features.login.model.LoginFormState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginScreenViewModel (
    private val previewMode: Boolean = false
) : ViewModel() {

    val repository = if (previewMode) null else AuthRepository()
    private val _uiState = MutableStateFlow(LoginFormState())
    val uiState: StateFlow<LoginFormState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail) }
    }

    fun onPasswordChange(newPass: String) {
        _uiState.update { it.copy(pass = newPass) }
    }

    fun onLogin() {

        if (previewMode) return

        val state = _uiState.value
        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true, isSuccess = false, errorMessage = null) }

            val result = repository?.signIn(
                email = state.email,
                password = state.pass
            )

            result?.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    // TODO: navegar para a próxima tela
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "Unknow error") }
                }
            )

        }


    }

}