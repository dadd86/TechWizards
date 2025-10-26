package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import com.diegodiaz.techwizards.domain.model.Match
import com.diegodiaz.techwizards.domain.model.MatchParticipant
import com.diegodiaz.techwizards.domain.model.MatchScore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MatchUiState(
    val match: Match? = null,
    val participantes: List<MatchParticipant> = emptyList(),
    val puntuaciones: List<MatchScore> = emptyList(),
    val error: String? = null
)

class ControladorMatch : ViewModel() {

    private val _ui = MutableStateFlow(MatchUiState())
    val ui: StateFlow<MatchUiState> = _ui.asStateFlow()

    fun crearMatch(lobbyId: Long?) {
        val m = Match(
            id = System.currentTimeMillis(),
            lobbyId = lobbyId,
            status = Match.Status.CREATED
        )
        _ui.value = _ui.value.copy(match = m, participantes = emptyList(), puntuaciones = emptyList())
    }

    fun addParticipante(userId: Long, apodo: String? = null) {
        val m = _ui.value.match ?: return
        val p = MatchParticipant(
            id = System.currentTimeMillis(),
            matchId = m.id,
            userId = userId,
            apodo = apodo
        )
        _ui.value = _ui.value.copy(participantes = _ui.value.participantes + p, puntuaciones = _ui.value.puntuaciones + MatchScore(id = System.currentTimeMillis()+1, matchId = m.id, participantId = p.id, puntos = 0))
    }

    fun iniciar() {
        val m = _ui.value.match ?: return
        _ui.value = _ui.value.copy(match = m.copy(status = Match.Status.RUNNING))
    }

    fun finalizar() {
        val m = _ui.value.match ?: return
        _ui.value = _ui.value.copy(match = m.copy(status = Match.Status.FINISHED, finEn = System.currentTimeMillis()))
    }

    fun sumarPuntos(participantId: Long, delta: Int) {
        val scores = _ui.value.puntuaciones.map { s -> if (s.participantId == participantId) s.copy(puntos = (s.puntos + delta)) else s }
        _ui.value = _ui.value.copy(puntuaciones = scores)
    }

    fun limpiarError() { _ui.value = _ui.value.copy(error = null) }
}
