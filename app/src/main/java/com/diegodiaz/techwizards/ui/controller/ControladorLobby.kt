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
        val codigoNormalizado = normalizarCodigoIngreso(codigo)
        val lobbyId = codigoNormalizado ?: System.currentTimeMillis().toString()
        val nuevo = Lobby(
            id = lobbyId,
            nombre = nombre,
            codigo = codigoNormalizado,
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

    /**
     * Normaliza el código de lobby ingresado para evitar caracteres inválidos.
     *
     * @param codigo Texto crudo ingresado por el usuario.
     * @return Código limpio o `null` si queda vacío tras limpiar.
     * @security No persiste ni registra el contenido; solo sanitiza localmente.
     */
    fun normalizarCodigoIngreso(codigo: String?): String? {
        if (codigo.isNullOrBlank()) return null
        val limpio = codigo.trim().replace(Regex("[^A-Za-z0-9-]"), "")
        return limpio.ifBlank { null }
    }

    fun limpiarError() { _ui.value = _ui.value.copy(error = null) }
}
