package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.Partida

/**
 * Repositorio remoto para historial de partidas por jugador.
 */
interface PartidaHistoryRepository {

    suspend fun registrarPartida(
        firebaseUid: String,
        partida: Partida,
    ): Result<Unit, AgentError>

    /**
     * Obtiene historial remoto del jugador.
     *
     * @param firebaseUid UID autenticado de Firebase.
     * @param limit Máximo de elementos a recuperar.
     * @return Resultado con partidas remotas.
     * @security
     * - Limita consultas por UID y tamaño.
     */
    suspend fun obtenerHistorial(
        firebaseUid: String,
        limit: Int,
    ): Result<List<Partida>, AgentError>
}
