package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.CommonPrize
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry
import com.diegodiaz.techwizards.domain.model.Lobby
import com.diegodiaz.techwizards.domain.model.LobbyEstado
import com.diegodiaz.techwizards.domain.model.Match
import com.diegodiaz.techwizards.domain.model.MatchEstado
import com.diegodiaz.techwizards.domain.model.MatchParticipant
import com.diegodiaz.techwizards.domain.model.MatchScore
import com.diegodiaz.techwizards.domain.model.MatchSnapshot
import com.diegodiaz.techwizards.domain.repository.MatchRepository
import com.diegodiaz.techwizards.domain.repository.ScoreRepository
import com.diegodiaz.techwizards.data.remote.lobby.LobbyRealtimeFirebaseDataSource
import com.diegodiaz.techwizards.data.repository.impl.LobbyRepositoryRoom
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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
    val buscandoRival: Boolean = false,
    val error: String? = null
)

class ControladorMatchOnline(
    private val matchRepository: MatchRepository,
    private val scoreRepository: ScoreRepository,
    private val lobbyRepository: LobbyRepositoryRoom,
    private val lobbyRealtime: LobbyRealtimeFirebaseDataSource
) : ViewModel() {

    private val _ui = MutableStateFlow(MatchOnlineUiState())
    val ui: StateFlow<MatchOnlineUiState> = _ui.asStateFlow()

    private var streamJob: Job? = null
    private var lobbyStreamJob: Job? = null
    private var buscarRivalJob: Job? = null
    private var ultimoLanzamientoProcesado: Map<Long, Int> = emptyMap()

    /**
     * Crea un match a partir de un lobby ya existente y persistido.
     *
     * @param lobby Lobby origen a persistir localmente si es necesario.
     * @param creadorNumero Identificador numérico del creador.
     * @param modo Modalidad de juego.
     * @security No expone PII; persiste solo identificadores internos.
     */
    fun crearMatchDesdeLobby(lobby: Lobby, creadorNumero: Long, modo: String = "duelo") {
        viewModelScope.launch {
            lobbyRepository.upsertLobby(lobby)
            crearMatchDesdeLobbyId(lobbyId = lobby.id, creadorNumero = creadorNumero, modo = modo)
        }
    }

    /**
     * Crea un match usando el identificador del lobby.
     *
     * @param lobbyId Identificador del lobby.
     * @param creadorNumero Identificador numérico del creador.
     * @param modo Modalidad de juego.
     * @security No expone PII; usa identificadores internos.
     */
    private fun crearMatchDesdeLobbyId(lobbyId: String, creadorNumero: Long, modo: String = "duelo") {
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
            try {
                matchRepository.observarEstado(matchId).firstOrNull()?.let { snapshot ->
                    aplicarSnapshot(snapshot)
                }
            } catch (error: Exception) {
                _ui.value = _ui.value.copy(
                    error = errorToUiMessage(error),
                    cargando = false
                )
            }
        }

        streamJob?.cancel()
        streamJob = viewModelScope.launch {
            matchRepository.observarEstado(matchId)
                .catch { error ->
                    _ui.value = _ui.value.copy(
                        error = errorToUiMessage(error),
                        cargando = false,
                        buscandoRival = false
                    )
                }
                .collect { snapshot ->
                    aplicarSnapshot(snapshot)
                }
        }

        viewModelScope.launch { cargarTopYpremio() }
        observarPremioComun()
        iniciarEscuchaLobby(lobbyId)

        // Pre-carga el jugador local en la UI si viene desde un lobby.
        if (usuarioId != null) {
            agregarParticipanteLocal(matchId, usuarioId)
        }
    }

    private fun observarPremioComun() {
        viewModelScope.launch {
            scoreRepository.observarPremioComun()
                .catch { error ->
                    _ui.value = _ui.value.copy(
                        error = errorToUiMessage(error)
                    )
                }
                .collect { premio ->
                    val topTen = _ui.value.topTen
                    val progreso = calcularProgresoPremio(premio, topTen)
                    _ui.value = _ui.value.copy(
                        premioComun = premio,
                        progresoPremio = progreso
                    )
                }
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
    /**
     * Busca un rival en lobbies disponibles o crea uno nuevo y espera un minuto.
     *
     * @param usuarioNumero Identificador numérico del jugador local.
     * @security No expone datos sensibles; usa IDs internos y estado de lobby.
     */
    fun buscarRival(usuarioNumero: Long, lobbyIdOverride: String? = null) {
        if (_ui.value.buscandoRival) return
        buscarRivalJob?.cancel()
        _ui.value = _ui.value.copy(buscandoRival = true, error = null)
        viewModelScope.launch {
            val lobbyId = lobbyIdOverride?.takeIf { it.isNotBlank() } ?: _ui.value.lobbyId
            if (lobbyId.isNullOrBlank()) {
                _ui.value = _ui.value.copy(
                    buscandoRival = false,
                    error = "No se encontró lobby activo para buscar rival"
                )
                return@launch
            }

            runCatching {
                asegurarLobbyRemoto(lobbyId, usuarioNumero)
            }.onFailure { error ->
                _ui.value = _ui.value.copy(
                    buscandoRival = false,
                    error = error.message ?: "Error al unirse al lobby remoto"
                )
                return@launch
            }
            iniciarEscuchaLobby(lobbyId)
            iniciarTimeoutRival()
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
            _ui.value = _ui.value.copy(
                topTen = top,
                premioComun = premio,
                progresoPremio = calcularProgresoPremio(premio, top)
            )
        } catch (error: Exception) {
            _ui.value = _ui.value.copy(error = errorToUiMessage(error))
        }
    }

    private fun iniciarTimeoutRival() {
        buscarRivalJob?.cancel()
        buscarRivalJob = viewModelScope.launch {
            delay(TIEMPO_ESPERA_RIVAL_MS)
            if (!_ui.value.remotoListo) {
                _ui.value = _ui.value.copy(
                    buscandoRival = false,
                    error = "No encontramos rival en este momento"
                )
            }
        }
    }

    private fun iniciarEscuchaLobby(lobbyId: String?) {
        if (lobbyId.isNullOrBlank()) return
        lobbyStreamJob?.cancel()
        lobbyStreamJob = viewModelScope.launch {
            lobbyRealtime.streamLobby(lobbyId).collect { snapshot ->
                val jugadores = snapshot?.jugadoresConectados?.size ?: 0
                val rivalListo = jugadores >= 2
                if (rivalListo && _ui.value.buscandoRival) {
                    buscarRivalJob?.cancel()
                }
                _ui.value = _ui.value.copy(
                    remotoListo = _ui.value.remotoListo || rivalListo,
                    buscandoRival = _ui.value.buscandoRival && !rivalListo
                )
            }
        }
    }

    private suspend fun asegurarLobbyRemoto(lobbyId: String, creadorNumero: Long) {
        val lobby = Lobby(
            id = lobbyId,
            nombre = "Lobby $creadorNumero",
            codigo = lobbyId,
            modo = "duelo",
            estado = LobbyEstado.PENDING,
            creadorNumero = creadorNumero,
            createdAtMs = System.currentTimeMillis()
        )
        lobbyRealtime.crearLobby(lobby)
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
        if (snapshot.remotoListo || snapshot.participantes.size >= 2) {
            buscarRivalJob?.cancel()
        }
        val remotoListo = snapshot.remotoListo || _ui.value.remotoListo
        _ui.value = _ui.value.copy(
            match = snapshot.match,
            participantes = participantes,
            puntuaciones = marcador,
            remotoListo = remotoListo,
            carasSeleccionadas = snapshot.carasElegidas,
            lanzamientos = snapshot.lanzamientos,
            ganadorRonda = snapshot.ganadorRonda,
            huboEmpate = snapshot.empate,
            cargando = false,
            buscandoRival = _ui.value.buscandoRival && !remotoListo
        )
    }

    private fun calcularProgresoPremio(
        premio: CommonPrize?,
        topTen: List<LeaderboardEntry>
    ): Float {
        val objetivo = premio?.valor ?: return 0f
        if (objetivo <= 0) return 0f
        val avance = topTen.firstOrNull()?.score ?: 0
        return (avance.toFloat() / objetivo).coerceIn(0f, 1f)
    }
}
private fun errorToUiMessage(error: Any?): String {
    return when (error) {
        null -> "Error desconocido"
        is Throwable -> error.message ?: error::class.java.simpleName
        else -> error.toString()
    }

}

private const val TIEMPO_ESPERA_RIVAL_MS = 60_000L