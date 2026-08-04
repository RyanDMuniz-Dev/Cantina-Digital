package com.example.cantinadigital.ui.features.stock.model

interface UpdateProductUiState {
    object Idle    : UpdateProductUiState
    object Loading : UpdateProductUiState
    object Success : UpdateProductUiState
    data class Error(val message: String) : UpdateProductUiState
}