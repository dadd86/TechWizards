package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.Match
import com.diegodiaz.techwizards.domain.model.MatchParticipant
import com.diegodiaz.techwizards.domain.model.MatchScore
import com.diegodiaz.techwizards.domain.repository.MatchRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Gestiona la pantalla de match combinando estado local y streams remotos.
 *
 * @property matchRepository Fuente remota via Retrofit + Firestore/WS.
 */
data class MatchUiState(
    val matchId: String? = null,
    val lobbyId: String? = null,
    val match: Match? = null,
    val participantes: List<MatchParticipant> = emptyList(),
    val puntuaciones: List<MatchScore> = emptyList(),
    val seleccionCara: Int = 1,
    val resultadoDado: Int? = null,
    val remotoListo: Boolean = false,
    val localListo: Boolean = false,
    val cargando: Boolean = false,
    val error: String? = null
)

class ControladorMatchOnline(
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(MatchUiState())
    val ui: StateFlow<MatchUiState> = _ui.asStateFlow()

    private var streamJob: Job? = null

    fun iniciar(matchId: String, lobbyId: String?, jugadorNumero: Long?) {
        if (matchId == _ui.value.matchId) return
        _ui.value = _ui.value.copy(matchId = matchId, lobbyId = lobbyId, cargando = true)

        streamJob?.cancel()
        streamJob = viewModelScope.launch {
            matchRepository.observarEstado(matchId).collect { snapshot ->
                _ui.value = _ui.value.copy(
                    match = snapshot.match,
                    participantes = snapshot.participantes,
                    puntuaciones = snapshot.scores,
                    remotoListo = snapshot.remotoListo,
                    cargando = false
                )
            }
        }

        // Pre-carga el jugador local en la UI si viene desde un lobby.
        if (jugadorNumero != null) {
            agregarParticipanteLocal(matchId, jugadorNumero)
        }
    }

    fun seleccionarCara(cara: Int) {
        val nuevaCara = cara.coerceIn(1, 6)
        _ui.value = _ui.value.copy(seleccionCara = nuevaCara, error = null)
    }

    fun confirmarApuesta(jugadorNumero: Long) {
        val matchId = _ui.value.matchId ?: return
        viewModelScope.launch {
            when (val resultado = matchRepository.marcarListo(matchId, jugadorNumero, _ui.value.seleccionCara)) {
                is Result.Err -> {
                    _ui.value = _ui.value.copy(error = resultado.error.message)
                }
                is Result.Ok -> {
                    _ui.value = _ui.value.copy(localListo = true)
                }
            }
        }
    }

    fun lanzarDado(jugadorNumero: Long) {
        val matchId = _ui.value.matchId ?: return
        val resultado = (1..6).random()
        _ui.value = _ui.value.copy(resultadoDado = resultado)
        viewModelScope.launch {
            when (val resultadoLanzamiento = matchRepository.registrarLanzamiento(matchId, jugadorNumero, resultado)) {
                is Result.Err -> {
                    _ui.value = _ui.value.copy(error = resultadoLanzamiento.error.message)
                }
                else -> Unit
            }
        }
    }

    fun limpiarError() {
        _ui.value = _ui.value.copy(error = null)
    }

    private fun agregarParticipanteLocal(matchId: String, jugadorNumero: Long) {
        val existente = _ui.value.participantes
        if (existente.any { it.usuarioNumero == jugadorNumero }) return
        val nuevo = MatchParticipant(
            matchId = matchId,
            usuarioNumero = jugadorNumero,
            rol = "local",
            teamId = null,
            joinedAtMs = System.currentTimeMillis(),
            leftAtMs = null,
            score = 0
        )
        _ui.value = _ui.value.copy(participantes = existente + nuevo)
    }
}