package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.credenciales.CredentialsStore
import com.diegodiaz.techwizards.data.remote.api.ScoresApi
import com.diegodiaz.techwizards.data.remote.mapper.ScoreRemoteMapper
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry

/**
 * Implementación remota de acceso a puntuaciones usando ScoresApi.
 *
 * Esta clase:
 * - Recupera el top 10 de puntuaciones
 * - Publica nuevas puntuaciones
 * - Usa autenticación basada en token almacenado en CredentialsStore
 *
 * NO gestiona premio común ni login (eso va por ScoreRepository).
 */
class ScoresRepositoryRemote(
    private val scoresApi: ScoresApi,
    private val mapper: ScoreRemoteMapper,
    private val credentialsStore: CredentialsStore
) {

    /**
     * Recupera el top 10 del backend remoto.
     */
    suspend fun getTopTen(): List<LeaderboardEntry> {
        val remoteScores = scoresApi.getTopTen()
        return remoteScores.mapIndexed { index, dto ->
            mapper.toDomain(dto, position = index + 1)
        }
    }

    /**
     * Publica una nueva puntuación en nombre del usuario autenticado.
     */
    suspend fun publishScore(score: Int) {
        val token = credentialsStore.obtenerFirebaseToken()

        scoresApi.publishScore(
            bearer = token?.let { "Bearer $it" },
            score = score
        )
    }
}
