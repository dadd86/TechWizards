package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import com.diegodiaz.techwizards.domain.model.*
import androidx.lifecycle.viewModelScope
import com.diegodiaz.techwizards.domain.model.CommonPrize
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry
import com.diegodiaz.techwizards.domain.model.Match
import com.diegodiaz.techwizards.domain.model.MatchEstado
import com.diegodiaz.techwizards.domain.model.MatchParticipant
import com.diegodiaz.techwizards.domain.model.MatchScore
import com.diegodiaz.techwizards.domain.repository.ScoreRepository
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MatchUiState(
    val match: Match? = null,
    val participantes: List<MatchParticipant> = emptyList(),
    val puntuaciones: List<MatchScore> = emptyList(),
    val topTen: List<LeaderboardEntry> = emptyList(),
    val premioComun: CommonPrize? = null,
    val progresoPremio: Float = 0f,
    val actualizadoEnMs: Long? = null,
    val error: String? = null
)

class ControladorMatch(
    private val scoreRepository: ScoreRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(MatchUiState())
    val ui: StateFlow<MatchUiState> = _ui.asStateFlow()

    init {
        refrescarEstadoOnline()
    }

    fun crearMatch(
        lobbyId: String?,
        modo: String,
        createdByNumero: Long
    ) {
        val m = Match(
            id = System.currentTimeMillis().toString(),
            lobbyId = lobbyId,
            modo = modo,
            estado = MatchEstado.PENDING,
            createdByNumero = createdByNumero,
            createdAtMs = System.currentTimeMillis(),
            startedAtMs = null,
            finishedAtMs = null
        )
        _ui.value = _ui.value.copy(match = m, participantes = emptyList(), puntuaciones = emptyList())
    }

    fun addParticipante(
        usuarioNumero: Long,
        rol: String? = null,
        teamId: String? = null
    ) {
        val m = _ui.value.match ?: return
        val now = System.currentTimeMillis()
        val p = MatchParticipant(
            matchId = m.id,
            usuarioNumero = usuarioNumero,
            rol = rol,
            teamId = teamId,
            joinedAtMs = now,
            leftAtMs = null,
            score = 0
        )
        // Añade el participante y una puntuación inicial para ese usuario
        _ui.value = _ui.value.copy(
            participantes = _ui.value.participantes + p,
            puntuaciones = _ui.value.puntuaciones + MatchScore(matchId = m.id, usuarioNumero = usuarioNumero, score = 0)
        )
    }

    fun iniciar() {
        val m = _ui.value.match ?: return
        _ui.value = _ui.value.copy(match = m.copy(estado = MatchEstado.ACTIVE, startedAtMs = System.currentTimeMillis()))
    }

    fun finalizar() {
        val m = _ui.value.match ?: return
        _ui.value = _ui.value.copy(match = m.copy(estado = MatchEstado.FINISHED, finishedAtMs = System.currentTimeMillis()))
    }

    fun sumarPuntos(usuarioNumero: Long, delta: Int) {
        val scores = _ui.value.puntuaciones.map { s ->
            if (s.usuarioNumero == usuarioNumero) s.copy(score = s.score + delta) else s
        }
        _ui.value = _ui.value.copy(puntuaciones = scores)
    }

    fun limpiarError() {
        _ui.value = _ui.value.copy(error = null)
    }

    fun refrescarEstadoOnline() {
        viewModelScope.launch {
            runCatching {
                val topTen = scoreRepository.obtenerTopTen()
                val premio = scoreRepository.obtenerPremioComun()
                val progreso = calcularProgresoPremio(premio, topTen)

                _ui.value.copy(
                    topTen = topTen,
                    premioComun = premio,
                    progresoPremio = progreso,
                    actualizadoEnMs = System.currentTimeMillis(),
                    error = null
                )
            }.onSuccess { nuevoEstado ->
                _ui.value = nuevoEstado
            }.onFailure { error ->
                DecentralizedLogger.e("ControladorMatch", "No se pudo cargar datos online", error)
                _ui.value = _ui.value.copy(error = "No se pudo cargar ranking online")
            }
        }
    }

    private fun calcularProgresoPremio(premio: CommonPrize?, topTen: List<LeaderboardEntry>): Float {
        val objetivo = premio?.valor ?: return 0f
        if (objetivo <= 0) return 0f
        val avance = topTen.firstOrNull()?.score ?: 0
        return (avance.toFloat() / objetivo).coerceIn(0f, 1f)
    }
}
