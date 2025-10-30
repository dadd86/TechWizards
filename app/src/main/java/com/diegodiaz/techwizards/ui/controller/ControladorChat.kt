package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegodiaz.techwizards.domain.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatUiState(
    val mensajes: List<Message> = emptyList(),
    val textoActual: String = "",
    val error: String? = null
)

class ControladorChat : ViewModel() {
    private val _ui = MutableStateFlow(ChatUiState())
    val ui: StateFlow<ChatUiState> = _ui.asStateFlow()

    fun escribir(texto: String) {
        _ui.value = _ui.value.copy(textoActual = texto)
    }

    fun enviar(remitenteId: String, matchId: String) {
        val texto = _ui.value.textoActual.trim()
        if (texto.isBlank()) return

        // Message exige senderNumero: Long → convertimos remitenteId a Long
        val senderNumero = remitenteId.toLongOrNull()
        if (senderNumero == null) {
            _ui.value = _ui.value.copy(error = "remitenteId no es numérico (esperado Long).")
            return
        }

        viewModelScope.launch {
            val ahora = System.currentTimeMillis()
            val nuevo = Message(
                id = ahora.toString(),
                matchId = matchId,
                senderNumero = senderNumero,
                text = texto,
                createdAtMs = ahora
            )
            _ui.value = _ui.value.copy(
                mensajes = _ui.value.mensajes + nuevo,
                textoActual = ""
            )
        }
    }

    fun limpiarError() {
        _ui.value = _ui.value.copy(error = null)
    }
}
