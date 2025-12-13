package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.domain.model.CommonPrize
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry
import com.diegodiaz.techwizards.domain.model.UserSession

/**
 * Contrato de acceso a ranking y premio común a través de la API remota.
 */
interface ScoreRepository {

    /** Top 10 global del leaderboard */
    suspend fun getTopTen(): List<LeaderboardEntry>

    /** Publica una puntuación en nombre de un usuario autenticado */
    suspend fun submitScore(session: UserSession, score: Int)

    /** Lee el premio común visible para todos */
    suspend fun getCommonPrize(): CommonPrize

    /** Actualiza el premio común (requiere autorización) */
    suspend fun updateCommonPrize(session: UserSession, newPrize: CommonPrize): CommonPrize

    /** Login por alias: el backend devuelve la sesión (token, etc.) */
    suspend fun authenticateAlias(alias: String): UserSession

    // --- Alias en español para integraciones existentes ---
    suspend fun obtenerTopTen(): List<LeaderboardEntry> = getTopTen()

    suspend fun publicarPuntuacion(session: UserSession, score: Int) {
        submitScore(session, score)
    }

    suspend fun obtenerPremioComun(): CommonPrize = getCommonPrize()

    suspend fun actualizarPremioComun(session: UserSession, prize: CommonPrize): CommonPrize {
        return updateCommonPrize(session, prize)
    }

    suspend fun autenticarAlias(alias: String): UserSession = authenticateAlias(alias)
}
