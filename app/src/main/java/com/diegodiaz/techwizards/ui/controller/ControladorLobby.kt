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
    val codigoIngreso: String = "",
    val error: String? = null
)

class ControladorLobby : ViewModel() {

    private val _ui = MutableStateFlow(LobbyUiState())
    val ui: StateFlow<LobbyUiState> = _ui.asStateFlow()

    fun crearLobby(
        nombre: String,
        modo: String,
        creadorNumero: Long,
        codigo: String? = null
    ): Lobby {
        val nuevo = Lobby(
            id = System.currentTimeMillis().toString(),
            nombre = nombre,
            codigo = codigo,
            modo = modo,
            estado = LobbyEstado.PENDING,
            creadorNumero = creadorNumero,
            createdAtMs = System.currentTimeMillis()
        )
        _ui.value = _ui.value.copy(
            lobbies = _ui.value.lobbies + nuevo,
            lobbyActual = nuevo
        )
        return nuevo
    }

    fun seleccionar(lobbyId: String?) {
        _ui.value = _ui.value.copy(
            lobbyActual = _ui.value.lobbies.find { it.id == lobbyId }
        )
    }
    fun actualizarCodigoIngreso(nuevoCodigo: String) {
        _ui.value = _ui.value.copy(codigoIngreso = nuevoCodigo)
    }

    fun limpiarError() { _ui.value = _ui.value.copy(error = null) }
}
