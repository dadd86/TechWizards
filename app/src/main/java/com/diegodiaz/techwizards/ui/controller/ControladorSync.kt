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

    fun enqueue(tipo: String, payload: String) {
        val item = Outbox(
            id = System.currentTimeMillis(),
            tipo = tipo,
            payload = payload
        )
        _ui.value = _ui.value.copy(outbox = _ui.value.outbox + item)
    }

    fun marcarEntregado(id: Long) {
        _ui.value = _ui.value.copy(outbox = _ui.value.outbox.map { if (it.id == id) it.copy(entregado = true) else it })
    }

    fun limpiarEntregados() {
        _ui.value = _ui.value.copy(outbox = _ui.value.outbox.filterNot { it.entregado })
    }

    fun limpiarError() { _ui.value = _ui.value.copy(error = null) }
}
