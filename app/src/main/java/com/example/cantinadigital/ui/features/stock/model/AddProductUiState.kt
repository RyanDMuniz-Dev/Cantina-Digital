package com.example.cantinadigital.ui.features.stock.model

sealed interface AddProductUiState {
    object Idle    : AddProductUiState
    object Loading : AddProductUiState
    object Success : AddProductUiState
    data class Error(val message: String) : AddProductUiState
}