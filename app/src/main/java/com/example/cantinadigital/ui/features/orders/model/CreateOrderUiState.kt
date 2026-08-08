package com.example.cantinadigital.ui.features.orders.model

sealed interface CreateOrderUiState {
    object Idle    : CreateOrderUiState
    object Loading : CreateOrderUiState
    object Success : CreateOrderUiState
    data class Error(val message: String) : CreateOrderUiState
}