package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegodiaz.techwizards.data.remote.lobby.LobbyRealtimeFirebaseDataSource
import com.diegodiaz.techwizards.domain.model.Lobby
import com.diegodiaz.techwizards.domain.model.LobbyEstado
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale


data class LobbyUiState(
    val lobbies: List<Lobby> = emptyList(),
    val lobbyActual: Lobby? = null,
    val codigoIngreso: String = "",
    val jugadoresConectados: Int = 0,
    val rivalConectado: Boolean = false,
    val error: String? = null
)

class ControladorLobby(
    private val lobbyRealtime: LobbyRealtimeFirebaseDataSource
) : ViewModel() {

    private val _ui = MutableStateFlow(LobbyUiState())
    val ui: StateFlow<LobbyUiState> = _ui.asStateFlow()
    private var lobbyListenerJob: Job? = null

    fun crearLobby(
        nombre: String,
        modo: String,
        creadorNumero: Long,
        codigo: String? = null
    ): Lobby {
        val codigoNormalizado = normalizarCodigoIngreso(codigo)
        val now = System.currentTimeMillis()
        val lobbyId = codigoNormalizado ?: "lobby_$now"
        val nuevo = Lobby(
            id = lobbyId,
            nombre = nombre,
            codigo = codigoNormalizado,
            modo = modo,
            estado = LobbyEstado.PENDING,
            creadorNumero = creadorNumero,
            createdAtMs = now
        )
        _ui.value = _ui.value.copy(
            lobbies = _ui.value.lobbies + nuevo,
            lobbyActual = nuevo
        )

        crearLobbyRemoto(nuevo)
        observarLobbyRemoto(lobbyId)
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

    fun unirseLobbyRemoto(lobbyId: String, usuarioNumero: Long) {
        observarLobbyRemoto(lobbyId)
        viewModelScope.launch {
            runCatching {
                lobbyRealtime.unirseLobby(lobbyId, usuarioNumero)
            }.onFailure { error ->
                _ui.value = _ui.value.copy(error = error.message)
            }
        }
    }

    /**
     * Normaliza el código de lobby ingresado para evitar caracteres inválidos.
     *
     * @param codigo Texto crudo ingresado por el usuario.
     * @return Código limpio o `null` si queda vacío tras limpiar.
     * @security No persiste ni registra el contenido; solo sanitiza localmente.
     */
    fun normalizarCodigoIngreso(codigo: String?): String? {
        if (codigo.isNullOrBlank()) return null
        val limpio = codigo.trim()
            .replace(Regex("[^A-Za-z0-9-]"), "")
            .uppercase(Locale.ROOT)
        return limpio.ifBlank { null }
    }

    private fun crearLobbyRemoto(lobby: Lobby) {
        viewModelScope.launch {
            runCatching {
                lobbyRealtime.crearLobby(lobby)
            }.onFailure { error ->
                _ui.value = _ui.value.copy(error = error.message)
            }
        }
    }

    private fun observarLobbyRemoto(lobbyId: String) {
        lobbyListenerJob?.cancel()
        lobbyListenerJob = viewModelScope.launch {
            lobbyRealtime.streamLobby(lobbyId).collect { snapshot ->
                val jugadores = snapshot?.jugadoresConectados?.size ?: 0
                _ui.value = _ui.value.copy(
                    jugadoresConectados = jugadores,
                    rivalConectado = jugadores >= 2
                )
            }
        }
    }

    fun limpiarError() { _ui.value = _ui.value.copy(error = null) }
}
