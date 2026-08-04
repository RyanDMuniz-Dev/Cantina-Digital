package com.example.cantinadigital.ui.features.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cantinadigital.data.repository.AuthRepository
import com.example.cantinadigital.ui.features.signup.model.SignUpFormState
import com.example.cantinadigital.ui.features.signup.model.ThirdYearClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpScreenViewModel (
    private val previewMode: Boolean = false
) : ViewModel() {

    private val repository = if (previewMode) null else AuthRepository()

    private val _uiState = MutableStateFlow(SignUpFormState())
    val uiState: StateFlow<SignUpFormState> = _uiState.asStateFlow()

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun onClassChange(newClass: ThirdYearClass) {
        _uiState.update { it.copy(studentClass = newClass) }
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword) }
    }

    fun onSignUp() {

        if (previewMode) return

        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository?.signUp(
                fullName = state.name,
                email = state.email,
                password = state.password,
                studentClass = state.studentClass.name
            )

            result?.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    // TODO: navegar para a próxima tela
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Unknow Error"
                        )
                    }
                }
            )
        }
    }

}