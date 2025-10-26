package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import com.diegodiaz.techwizards.domain.model.Lobby
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LobbyUiState(
    val lobbies: List<Lobby> = emptyList(),
    val lobbyActual: Lobby? = null,
    val error: String? = null
)

class ControladorLobby : ViewModel() {

    private val _ui = MutableStateFlow(LobbyUiState())
    val ui: StateFlow<LobbyUiState> = _ui.asStateFlow()

    fun crearLobby(nombre: String, ownerId: Long) {
        val nuevo = Lobby(
            id = System.currentTimeMillis(),
            nombre = nombre,
            ownerId = ownerId,
            participanteIds = listOf(ownerId)
        )
        _ui.value = _ui.value.copy(lobbies = _ui.value.lobbies + nuevo, lobbyActual = nuevo)
    }

    fun entrarLobby(lobbyId: Long, userId: Long) {
        val lobby = _ui.value.lobbies.find { it.id == lobbyId } ?: return
        val actualizado = lobby.copy(participanteIds = (lobby.participanteIds + userId).distinct())
        _ui.value = _ui.value.copy(
            lobbies = _ui.value.lobbies.map { if (it.id == lobbyId) actualizado else it },
            lobbyActual = actualizado
        )
    }

    fun salirLobby(userId: Long) {
        val lobby = _ui.value.lobbyActual ?: return
        val actualizado = lobby.copy(participanteIds = lobby.participanteIds.filterNot { it == userId })
        _ui.value = _ui.value.copy(
            lobbies = _ui.value.lobbies.map { if (it.id == lobby.id) actualizado else it },
            lobbyActual = if (actualizado.participanteIds.isEmpty()) null else actualizado
        )
    }

    fun seleccionar(lobbyId: Long?) {
        _ui.value = _ui.value.copy(lobbyActual = _ui.value.lobbies.find { it.id == lobbyId })
    }

    fun limpiarError() { _ui.value = _ui.value.copy(error = null) }
}
