package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegodiaz.techwizards.domain.model.Match
import com.diegodiaz.techwizards.domain.model.MatchEstado
import com.diegodiaz.techwizards.domain.model.MatchParticipant
import com.diegodiaz.techwizards.domain.model.MatchScore
import com.diegodiaz.techwizards.domain.model.MatchEvent
import com.diegodiaz.techwizards.domain.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MatchUiState(
    val match: Match? = null,
    val participantes: List<MatchParticipant> = emptyList(),
    val puntuaciones: List<MatchScore> = emptyList(),
    // Solo UI: apodos por usuarioNumero (el dominio no tiene apodo)
    val apodosPorUsuario: Map<Long, String?> = emptyMap(),
    val error: String? = null,
    val cargando: Boolean = false
)

class ControladorMatch(
    private val repo: MatchRepository,
    // número (Long) del usuario actual que crea/inicia el match
    private val usuarioNumeroActual: Long
) : ViewModel() {

    private val _ui = MutableStateFlow(MatchUiState())
    val ui: StateFlow<MatchUiState> = _ui.asStateFlow()

    /** Crea un Match (dominio correcto) y lo persiste. */
    fun crearMatch(lobbyId: String?, modo: String = "1v1") {
        val ahora = System.currentTimeMillis()
        val m = Match(
            id = ahora.toString(),      // dominio: String
            lobbyId = lobbyId,          // dominio: String?
            modo = modo,
            estado = MatchEstado.PENDING,
            createdByNumero = usuarioNumeroActual,
            createdAtMs = ahora,
            startedAtMs = null,
            finishedAtMs = null
        )
        _ui.value = _ui.value.copy(match = m, participantes = emptyList(), puntuaciones = emptyList())

        // Persistencia (ignoramos la forma de Result para compilar ya)
        viewModelScope.launch {
            try {
                repo.upsertMatch(m)
                repo.registrarEvento(
                    MatchEvent(
                        id = (ahora + 1).toString(),
                        matchId = m.id,
                        seq = 1L,
                        type = "MATCH_CREATED",
                        actorNumero = usuarioNumeroActual,
                        payloadJson = """{"modo":"$modo","lobbyId":${if (lobbyId == null) "null" else "\"$lobbyId\""}}""",
                        createdAtMs = System.currentTimeMillis()
                    )
                )
            } catch (t: Throwable) {
                _ui.value = _ui.value.copy(error = t.message)
            }
        }
    }

    /** Añade participante (apodo queda solo en UI) y registra evento. */
    fun addParticipante(
        usuarioNumero: Long,
        apodo: String? = null,
        rol: String? = null,
        teamId: String? = null
    ) {
        val m = _ui.value.match ?: return
        val ahora = System.currentTimeMillis()
        val p = MatchParticipant(
            matchId = m.id,
            usuarioNumero = usuarioNumero,
            rol = rol,
            teamId = teamId,
            joinedAtMs = ahora,
            leftAtMs = null,
            score = 0
        )
        val uiPrev = _ui.value
        _ui.value = uiPrev.copy(
            participantes = uiPrev.participantes + p,
            // mantenemos un acumulado por si quieres consultar rápido
            puntuaciones = if (uiPrev.puntuaciones.any { it.usuarioNumero == usuarioNumero }) {
                uiPrev.puntuaciones
            } else {
                uiPrev.puntuaciones + MatchScore(matchId = m.id, usuarioNumero = usuarioNumero, score = 0)
            },
            apodosPorUsuario = uiPrev.apodosPorUsuario + (usuarioNumero to apodo)
        )

        viewModelScope.launch {
            try {
                repo.registrarEvento(
                    MatchEvent(
                        id = (ahora).toString(),
                        matchId = m.id,
                        seq = ahora, // simplificación: usamos timestamp como secuencia
                        type = "PARTICIPANT_JOINED",
                        actorNumero = usuarioNumero,
                        payloadJson = """{"rol":${rol?.let { "\"$it\"" } ?: "null"},"teamId":${teamId?.let { "\"$it\"" } ?: "null"},"apodo":${apodo?.let { "\"$it\"" } ?: "null"}}""",
                        createdAtMs = ahora
                    )
                )
            } catch (t: Throwable) {
                _ui.value = _ui.value.copy(error = t.message)
            }
        }
    }

    /** Pasa a ACTIVE, fija startedAtMs y persiste + evento. */
    fun iniciar() {
        val m = _ui.value.match ?: return
        val ahora = System.currentTimeMillis()
        val actualizado = m.copy(estado = MatchEstado.ACTIVE, startedAtMs = ahora)
        _ui.value = _ui.value.copy(match = actualizado)

        viewModelScope.launch {
            try {
                repo.upsertMatch(actualizado)
                repo.registrarEvento(
                    MatchEvent(
                        id = (ahora).toString(),
                        matchId = actualizado.id,
                        seq = ahora,
                        type = "MATCH_STARTED",
                        actorNumero = usuarioNumeroActual,
                        payloadJson = null,
                        createdAtMs = ahora
                    )
                )
            } catch (t: Throwable) {
                _ui.value = _ui.value.copy(error = t.message)
            }
        }
    }

    /** Pasa a FINISHED, fija finishedAtMs, guarda marcadores y persiste + evento. */
    fun finalizar() {
        val m = _ui.value.match ?: return
        val ahora = System.currentTimeMillis()
        val actualizado = m.copy(estado = MatchEstado.FINISHED, finishedAtMs = ahora)
        _ui.value = _ui.value.copy(match = actualizado)

        viewModelScope.launch {
            try {
                // Persistir match
                repo.upsertMatch(actualizado)

                // Guardar score final por usuario (desde participantes UI)
                val finales = _ui.value.participantes.groupBy { it.usuarioNumero }
                    .map { (usuario, parts) ->
                        // tomamos el máximo score entre entradas del mismo usuario (o la suma si prefieres)
                        val scoreFinal = parts.maxOfOrNull { it.score } ?: 0
                        MatchScore(matchId = actualizado.id, usuarioNumero = usuario, score = scoreFinal)
                    }

                // persistimos cada score (ignoramos forma de Result)
                finales.forEach { repo.guardarScore(it) }

                // Evento de fin
                repo.registrarEvento(
                    MatchEvent(
                        id = (ahora).toString(),
                        matchId = actualizado.id,
                        seq = ahora,
                        type = "MATCH_FINISHED",
                        actorNumero = usuarioNumeroActual,
                        payloadJson = null,
                        createdAtMs = ahora
                    )
                )

                // reflejamos en UI la lista final de puntuaciones
                _ui.value = _ui.value.copy(puntuaciones = finales)
            } catch (t: Throwable) {
                _ui.value = _ui.value.copy(error = t.message)
            }
        }
    }

    /** Suma puntos a un usuario (actualiza participante y acumulado UI) y registra evento. */
    fun sumarPuntos(usuarioNumero: Long, delta: Int) {
        val uiPrev = _ui.value
        val m = uiPrev.match ?: return

        val participantesActualizados = uiPrev.participantes.map { p ->
            if (p.usuarioNumero == usuarioNumero) p.copy(score = p.score + delta) else p
        }
        val puntuacionesActualizadas = if (uiPrev.puntuaciones.any { it.usuarioNumero == usuarioNumero }) {
            uiPrev.puntuaciones.map { s ->
                if (s.usuarioNumero == usuarioNumero) s.copy(score = s.score + delta) else s
            }
        } else {
            uiPrev.puntuaciones + MatchScore(matchId = m.id, usuarioNumero = usuarioNumero, score = delta)
        }

        _ui.value = uiPrev.copy(participantes = participantesActualizados, puntuaciones = puntuacionesActualizadas)

        val ahora = System.currentTimeMillis()
        viewModelScope.launch {
            try {
                repo.registrarEvento(
                    MatchEvent(
                        id = (ahora).toString(),
                        matchId = m.id,
                        seq = ahora,
                        type = "SCORE_UPDATED",
                        actorNumero = usuarioNumero,
                        payloadJson = """{"delta":$delta}""",
                        createdAtMs = ahora
                    )
                )
            } catch (t: Throwable) {
                _ui.value = _ui.value.copy(error = t.message)
            }
        }
    }

    fun limpiarError() {
        _ui.value = _ui.value.copy(error = null)
    }
}
