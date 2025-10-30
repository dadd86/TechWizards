package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import com.diegodiaz.techwizards.domain.model.Lobby
import com.diegodiaz.techwizards.domain.model.LobbyEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LobbyUiState(
    val lobbies: List<Lobby> = emptyList(),
    val lobbyActual: Lobby? = null,
    val participantesPorLobby: Map<String, List<Long>> = emptyMap(),
    val error: String? = null
)

class ControladorLobby : ViewModel() {

    private val _ui = MutableStateFlow(LobbyUiState())
    val ui: StateFlow<LobbyUiState> = _ui.asStateFlow()

    fun crearLobby(
        nombre: String,
        ownerId: Long,
        modo: String = "1v1",
        codigo: String? = null
    ) {
        val ahora = System.currentTimeMillis()
        val nuevo = Lobby(
            id = ahora.toString(),
            nombre = nombre,
            codigo = codigo,
            modo = modo,
            estado = LobbyEstado.PENDING,
            creadorNumero = ownerId,
            createdAtMs = ahora
        )
        val ui = _ui.value
        _ui.value = ui.copy(
            lobbies = ui.lobbies + nuevo,
            lobbyActual = nuevo,
            participantesPorLobby = ui.participantesPorLobby + (nuevo.id to listOf(ownerId))
        )
    }

    fun entrarLobby(lobbyId: String, userId: Long) {
        val ui = _ui.value
        val lobby = ui.lobbies.find { it.id == lobbyId } ?: return
        val actuales = ui.participantesPorLobby[lobbyId].orEmpty()
        val nuevos = (actuales + userId).distinct()
        _ui.value = ui.copy(
            participantesPorLobby = ui.participantesPorLobby + (lobbyId to nuevos),
            lobbyActual = lobby
        )
    }

    fun salirLobby(userId: Long) {
        val ui = _ui.value
        val lobby = ui.lobbyActual ?: return
        val lobbyId = lobby.id
        val restantes = ui.participantesPorLobby[lobbyId].orEmpty().filterNot { it == userId }
        _ui.value = ui.copy(
            participantesPorLobby = ui.participantesPorLobby + (lobbyId to restantes),
            lobbyActual = if (restantes.isEmpty()) null else lobby
        )
    }

    fun seleccionar(lobbyId: String?) {
        _ui.value = _ui.value.copy(lobbyActual = _ui.value.lobbies.find { it.id == lobbyId })
    }

    fun limpiarError() {
        _ui.value = _ui.value.copy(error = null)
    }
}
