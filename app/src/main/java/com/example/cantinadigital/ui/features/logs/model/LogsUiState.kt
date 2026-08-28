package com.example.cantinadigital.ui.features.logs.model

import com.example.cantinadigital.data.model.AuditLog

data class LogsUiState(
    val logs: List<AuditLog> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
