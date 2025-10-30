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


    fun enqueue(tipo: String, payload: String, op: String = "UPSERT", entityId: String? = null) {
        val now = System.currentTimeMillis()
        val item = Outbox(
            operationId = now.toString(),
            entityType = tipo,
            entityId = entityId ?: "local:$now",
            op = op,
            payloadJson = payload,
            attempt = 0,
            lastError = null,
            createdAtMs = now,
            updatedAtMs = now
        )
        _ui.value = _ui.value.copy(
            outbox = _ui.value.outbox + item,
            ultimoResultado = "Encolada operación ${item.operationId}"
        )
    }

    fun marcarEntregado(id: Long) {
        val opId = id.toString()
        val now = System.currentTimeMillis()
        _ui.value = _ui.value.copy(
            outbox = _ui.value.outbox.map { o ->
                if (o.operationId == opId) o.copy(
                    attempt = o.attempt + 1,
                    lastError = null,
                    updatedAtMs = now
                ) else o
            },
            ultimoResultado = "Operación $opId marcada entregada"
        )
    }

    fun limpiarEntregados() {
        _ui.value = _ui.value.copy(
            outbox = _ui.value.outbox.filterNot { it.attempt > 0 && it.lastError == null },
            ultimoResultado = "Outbox limpiado"
        )
    }

    fun limpiarError() {
        _ui.value = _ui.value.copy(error = null)
    }
}
