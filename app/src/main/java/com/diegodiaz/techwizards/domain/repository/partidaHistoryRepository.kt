package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.Partida

/**
 * Repositorio remoto para historial de partidas por jugador.
 *
 * @security
 * - Opera únicamente con UID autenticado y datos de juego.
 */
interface PartidaHistoryRepository {

    /**
     * Registra una partida en Firebase para el jugador autenticado.
     *
     * @param firebaseUid UID del usuario autenticado.
     * @param partida Partida de dominio a persistir.
     * @return Resultado exitoso o error tipado.
     * @security
     * - No acepta tokens; usa UID ya autenticado.
     */
    suspend fun registrarPartida(
        firebaseUid: String,
        partida: Partida,
    ): Result<Unit, AgentError>
}