package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.Match
import com.diegodiaz.techwizards.domain.model.MatchEvent
import com.diegodiaz.techwizards.domain.model.MatchParticipant
import com.diegodiaz.techwizards.domain.model.MatchScore


/**
 * Contrato de acceso a datos de partidas multijugador.
 *
 * @security
 * - Las implementaciones deben usar el logger descentralizado y ejecutar I/O en `Dispatchers.IO`.
 */
interface MatchRepository {

    /**
     * Crea o actualiza un match junto con sus participantes iniciales.
     *
     * @param match Partida a persistir.
     * @param participantes Participantes iniciales.
     * @return Resultado vacío en éxito.
     * @security
     * - Validar que los participantes pertenecen al lobby autorizado.
     */
    suspend fun crearMatch(
        match: Match,
        participantes: List<MatchParticipant>,
    ): Result<Unit, AgentError>

    /**
     * Actualiza el estado y marcas de tiempo de un match existente.
     *
     * @param matchId Identificador del match.
     * @param estado Nuevo estado válido.
     * @param startedAtMs Marca de inicio opcional.
     * @param finishedAtMs Marca de fin opcional.
     * @security
     * - Debe validar transiciones de estado permitidas.
     */
    suspend fun actualizarEstado(
        matchId: String,
        estado: String,
        startedAtMs: Long?,
        finishedAtMs: Long?,
    ): Result<Unit, AgentError>

    /**
     * Registra un evento inmutable asociado al match.
     *
     * @param event Evento a persistir.
     * @security
     * - Sanitizar `payloadJson` previamente y evitar duplicidad de `seq`.
     */
    suspend fun registrarEvento(event: MatchEvent): Result<Unit, AgentError>

    /**
     * Actualiza o inserta los marcadores finales de un match.
     *
     * @param scores Lista de puntuaciones.
     * @security
     * - Los puntajes deben ser no negativos.
     */
    suspend fun registrarMarcadores(scores: List<MatchScore>): Result<Unit, AgentError>

    /**
     * Obtiene el historial de partidas finalizadas ordenado por fecha.
     *
     * @param limite Límite superior de resultados.
     * @return Resultado con la lista de partidas.
     * @security
     * - No debe exponer datos de jugadores ajenos al usuario activo.
     */
    suspend fun obtenerHistorial(limite: Int): Result<List<Match>, AgentError>
    /**
     * Persiste o actualiza la definición de un match.
     */
    suspend fun upsertMatch(match: Match): Result<Unit, AgentError>

    /**
     * Almacena el marcador final de un jugador.
     */
    suspend fun guardarScore(score: MatchScore): Result<Unit, AgentError>
}
