package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.CommonPrize
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry
import com.diegodiaz.techwizards.domain.model.Match
import com.diegodiaz.techwizards.domain.model.MatchEstado
import com.diegodiaz.techwizards.domain.model.MatchParticipant
import com.diegodiaz.techwizards.domain.model.MatchScore
import com.diegodiaz.techwizards.domain.model.MatchSnapshot
import com.diegodiaz.techwizards.domain.repository.MatchRepository
import com.diegodiaz.techwizards.domain.repository.ScoreRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * Gestiona la pantalla de match combinando estado local y streams remotos.
 *
 * @property matchRepository Fuente remota via Retrofit + Firestore/WS.
 */
data class MatchOnlineUiState(
    val matchId: String? = null,
    val lobbyId: String? = null,
    val match: Match? = null,
    val participantes: List<MatchParticipant> = emptyList(),
    val puntuaciones: List<MatchScore> = emptyList(),
    val topTen: List<LeaderboardEntry> = emptyList(),
    val premioComun: CommonPrize? = null,
    val progresoPremio: Float = 0f,
    val seleccionCara: Int = 1,
    val resultadoDado: Int? = null,
    val remotoListo: Boolean = false,
    val localListo: Boolean = false,
    val carasSeleccionadas: Map<Long, Int> = emptyMap(),
    val lanzamientos: Map<Long, Int> = emptyMap(),
    val ganadorRonda: Long? = null,
    val huboEmpate: Boolean = false,
    val cargando: Boolean = false,
    val error: String? = null
)

class ControladorMatchOnline(
    private val matchRepository: MatchRepository,
    private val scoreRepository: ScoreRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(MatchOnlineUiState())
    val ui: StateFlow<MatchOnlineUiState> = _ui.asStateFlow()

    private var streamJob: Job? = null
    private var ultimoLanzamientoProcesado: Map<Long, Int> = emptyMap()

    fun crearMatchDesdeLobby(lobbyId: String, creadorNumero: Long, modo: String = "duelo") {
        val matchId = "match-$lobbyId"
        val match = Match(
            id = matchId,
            lobbyId = lobbyId,
            modo = modo,
            estado = MatchEstado.PENDING,
            createdByNumero = creadorNumero,
            createdAtMs = System.currentTimeMillis(),
            startedAtMs = null,
            finishedAtMs = null
        )

        viewModelScope.launch {
            matchRepository.upsertMatch(match)
            iniciar(matchId = matchId, lobbyId = lobbyId, usuarioId = creadorNumero)
        }
    }

    fun unirseAMatchExistente(matchId: String, lobbyId: String?, usuarioId: Long) {
        viewModelScope.launch {
            // Garantiza que exista un registro inicial en caso de que el host aún no lo haya publicado
            matchRepository.upsertMatch(
                Match(
                    id = matchId,
                    lobbyId = lobbyId,
                    modo = "duelo",
                    estado = MatchEstado.PENDING,
                    createdByNumero = usuarioId,
                    createdAtMs = System.currentTimeMillis(),
                    startedAtMs = null,
                    finishedAtMs = null
                )
            )
            iniciar(matchId = matchId, lobbyId = lobbyId, usuarioId = usuarioId)
        }
    }

    fun iniciar(matchId: String, lobbyId: String?, usuarioId: Long?) {
        if (matchId == _ui.value.matchId) return
        _ui.value = _ui.value.copy(matchId = matchId, lobbyId = lobbyId, cargando = true)

        viewModelScope.launch {
            matchRepository.observarEstado(matchId).firstOrNull()?.let { snapshot ->
                aplicarSnapshot(snapshot)
            }
        }

        streamJob?.cancel()
        streamJob = viewModelScope.launch {
            matchRepository.observarEstado(matchId).collect { snapshot ->
                aplicarSnapshot(snapshot)
            }
        }

        viewModelScope.launch { cargarTopYpremio() }

        // Pre-carga el jugador local en la UI si viene desde un lobby.
        if (usuarioId != null) {
            agregarParticipanteLocal(matchId, usuarioId)
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
                    _ui.value = _ui.value.copy(error = errorToUiMessage(resultado.error))
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
                    _ui.value = _ui.value.copy(error = errorToUiMessage(resultadoLanzamiento.error))
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

    private suspend fun cargarTopYpremio() {
        try {
            val top = scoreRepository.obtenerTopTen()
            val premio = scoreRepository.obtenerPremioComun()
            _ui.value = _ui.value.copy(topTen = top, premioComun = premio)
        } catch (error: Exception) {
            _ui.value = _ui.value.copy(error = errorToUiMessage(error))
        }
    }

    private fun construirMarcador(
        snapshotScores: List<MatchScore>,
        lanzamientos: Map<Long, Int>,
        ganadorRonda: Long?,
        empate: Boolean
    ): List<MatchScore> {
        val matchId = _ui.value.matchId
        if (lanzamientos.isEmpty() || matchId == null) return snapshotScores.ifEmpty { _ui.value.puntuaciones }
        if (lanzamientos == ultimoLanzamientoProcesado) return _ui.value.puntuaciones
        ultimoLanzamientoProcesado = lanzamientos

        if (ganadorRonda == null || empate) return snapshotScores.ifEmpty { _ui.value.puntuaciones }

        val base = snapshotScores.ifEmpty { _ui.value.puntuaciones }
        val actualizadas = base.toMutableList()
        val indice = actualizadas.indexOfFirst { it.usuarioNumero == ganadorRonda }
        if (indice >= 0) {
            actualizadas[indice] = actualizadas[indice].copy(score = actualizadas[indice].score + 1)
        } else {
            actualizadas.add(
                MatchScore(
                    matchId = matchId,
                    usuarioNumero = ganadorRonda,
                    score = 1
                )
            )
        }
        return actualizadas
    }
    private fun aplicarSnapshot(snapshot: MatchSnapshot) {
        val participantes = (snapshot.participantes + _ui.value.participantes)
            .distinctBy { it.usuarioNumero }
        val marcador = construirMarcador(
            snapshotScores = snapshot.scores,
            lanzamientos = snapshot.lanzamientos,
            ganadorRonda = snapshot.ganadorRonda,
            empate = snapshot.empate
        )
        _ui.value = _ui.value.copy(
            match = snapshot.match,
            participantes = participantes,
            puntuaciones = marcador,
            remotoListo = snapshot.remotoListo,
            carasSeleccionadas = snapshot.carasElegidas,
            lanzamientos = snapshot.lanzamientos,
            ganadorRonda = snapshot.ganadorRonda,
            huboEmpate = snapshot.empate,
            cargando = false
        )
    }
}
private fun errorToUiMessage(error: Any?): String {
    return when (error) {
        null -> "Error desconocido"
        is Throwable -> error.message ?: error::class.java.simpleName
        else -> error.toString()
    }

}