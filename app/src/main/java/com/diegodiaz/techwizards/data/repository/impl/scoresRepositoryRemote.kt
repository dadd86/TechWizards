package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.credenciales.CredentialsStore
import com.diegodiaz.techwizards.data.remote.api.ScoresApi
import com.diegodiaz.techwizards.data.remote.dto.ScoreRemoteDto
import com.diegodiaz.techwizards.data.remote.mapper.ScoreRemoteMapper
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry

/**
 * Implementación remota (plural) para leaderboard usando ScoresApi.
 *
 * - obtenerTopTen(): llama a /scores/top enviando token por header.
 * - publicarScore(): publica ScoreRemoteDto y devuelve el registro enriquecido.
 */
class ScoresRepositoryRemote(
    private val scoresApi: ScoresApi,
    private val mapper: ScoreRemoteMapper,
    private val credentialsStore: CredentialsStore
) {

    suspend fun getTopTen(): List<LeaderboardEntry> {
        val token = credentialsStore.obtenerFirebaseToken()
            ?: return emptyList() // sin token -> sin llamada

        val bearer = "Bearer $token"

        val remoteScores = scoresApi.obtenerTopTen(bearerToken = bearer)

        return remoteScores.mapIndexed { index, dto ->
            mapper.toDomain(dto, position = index + 1)
        }
    }

    suspend fun publishScore(score: Int): LeaderboardEntry? {
        val token = credentialsStore.obtenerFirebaseToken() ?: return null
        val bearer = "Bearer $token"

        // El endpoint espera ScoreRemoteDto como body.
        // Como mínimo player/points; el resto puede ir null.
        val payload = ScoreRemoteDto(
            id = null,
            player = credentialsStore.obtenerAliasAutenticado() ?: "anon",
            points = score,
            position = null,
            prize = null
        )

        val saved: ScoreRemoteDto = scoresApi.publicarScore(
            bearerToken = bearer,
            score = payload
        )

        // Si backend devuelve posición, la usamos; si no, null
        val pos = saved.position
        return mapper.toDomain(saved, position = pos ?: 0)
    }
}
