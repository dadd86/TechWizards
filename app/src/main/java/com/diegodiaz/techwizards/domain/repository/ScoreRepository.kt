package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.domain.model.CommonPrize
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry
import com.diegodiaz.techwizards.domain.model.UserSession
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acceso a ranking y premio común a través de la API remota.
 */
interface ScoreRepository {

    /** Recupera el Top 10 global del leaderboard */
    suspend fun obtenerTopTen(): List<LeaderboardEntry>

    /** Publica una puntuación en nombre de un usuario autenticado */
    suspend fun publicarPuntuacion(
        session: UserSession,
        score: Int
    )

    /** Recupera el premio común visible para todos los jugadores */
    suspend fun obtenerPremioComun(): CommonPrize

    /**
     * Observa el premio común en tiempo real.
     *
     * @return flujo con el premio común actualizado.
     * @security No expone tokens; solo datos públicos del premio.
     */
    fun observarPremioComun(): Flow<CommonPrize>


    /** Actualiza el premio común (requiere autorización) */
    suspend fun actualizarPremioComun(
        session: UserSession,
        nuevoPremio: CommonPrize
    ): CommonPrize

    /** Login por alias: el backend devuelve la sesión (token, etc.) */
    suspend fun autenticarAlias(alias: String): UserSession

    /** Suma al premio común (bote global) */
    suspend fun incrementarPremioComun(
        session: UserSession,
        delta: Int
    ): CommonPrize

    /**
     * Reclama el premio común (cobra el bote global y lo resetea a 0)
     * @return monedas cobradas
     */
    suspend fun reclamarPremioComun(
        session: UserSession,
        claimId: String
    ): Int
}

