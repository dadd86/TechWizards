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

    fun enviar(autorId: Long = 0L, lobbyId: Long? = null, matchId: Long? = null) {
        val texto = _ui.value.textoActual.trim()
        if (texto.isBlank()) return
        viewModelScope.launch {
            val nuevo = Message(
                id = System.currentTimeMillis(),
                authorId = autorId,
                lobbyId = lobbyId,
                matchId = matchId,
                texto = texto
            )
            _ui.value = _ui.value.copy(
                mensajes = _ui.value.mensajes + nuevo,
                textoActual = ""
            )
        }
    }

    fun limpiarError() { _ui.value = _ui.value.copy(error = null) }
}
