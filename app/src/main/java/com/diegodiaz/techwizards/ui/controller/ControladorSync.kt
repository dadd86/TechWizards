package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import com.diegodiaz.techwizards.domain.model.Outbox
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SyncUiState(
    val outbox: List<Outbox> = emptyList(),
    val sincronizando: Boolean = false,
    val ultimoResultado: String? = null,
    val error: String? = null
)

class ControladorSync : ViewModel() {

    private val _ui = MutableStateFlow(SyncUiState())
    val ui: StateFlow<SyncUiState> = _ui.asStateFlow()

    fun enqueue(
        entityType: String,
        entityId: String,
        op: String,
        payloadJson: String,
    ) {
        val now = System.currentTimeMillis()
        val item = Outbox(
            operationId = now.toString(),
            entityType = entityType,
            entityId = entityId,
            op = op,
            payloadJson = payloadJson,
            attempt = 0,
            lastError = null,
            createdAtMs = now,
            updatedAtMs = now
        )
        _ui.value = _ui.value.copy(outbox = _ui.value.outbox + item)
    }

    fun marcarExitoso(operationId: String) {
        _ui.value = _ui.value.copy(
            outbox = _ui.value.outbox.map {
                if (it.operationId == operationId) it.copy(lastError = null) else it
            }
        )
    }

    fun marcarIntentoFallido(operationId: String, error: String) {
        _ui.value = _ui.value.copy(
            outbox = _ui.value.outbox.map {
                if (it.operationId == operationId) it.copy(
                    attempt = it.attempt + 1,
                    lastError = error,
                    updatedAtMs = System.currentTimeMillis()
                ) else it
            }
        )
    }

    fun limpiarError() { _ui.value = _ui.value.copy(error = null) }
}
