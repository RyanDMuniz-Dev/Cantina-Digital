package com.example.cantinadigital.ui.features.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cantinadigital.data.repository.AuditLogRepository
import com.example.cantinadigital.ui.features.logs.model.LogsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LogsViewModel (
    private val auditLogRepository: AuditLogRepository = AuditLogRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LogsUiState())
    val uiState: StateFlow<LogsUiState> = _uiState.asStateFlow()

    init {
        fetchLogs()
    }

    fun fetchLogs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                auditLogRepository.getLogsOnce()
            }.onSuccess { list ->
                println("LOGS_DEBUG: Sucesso! Foram encontrados ${list.size} logs.")
                _uiState.update { it.copy(logs = list, isLoading = false) }
            }.onFailure { exception ->
                println("LOGS_DEBUG: Erro ao buscar logs: ${exception.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = exception.localizedMessage ?: "Erro ao carregar logs"
                    )
                }
            }
        }
    }

}