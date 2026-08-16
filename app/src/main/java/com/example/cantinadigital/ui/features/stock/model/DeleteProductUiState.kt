package com.example.cantinadigital.ui.features.stock.model

interface DeleteProductUiState {
    object Idle    : DeleteProductUiState
    object Loading : DeleteProductUiState
    object Success : DeleteProductUiState
    data class Error(val message: String) : DeleteProductUiState
}