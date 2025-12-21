package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.core.SessionManager
import com.diegodiaz.techwizards.credenciales.CredentialsStore
import com.diegodiaz.techwizards.data.remote.score.ScoreApi
import com.diegodiaz.techwizards.data.remote.score.toDomain
import com.diegodiaz.techwizards.data.remote.score.toRequestDto
import com.diegodiaz.techwizards.domain.model.CommonPrize
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry
import com.diegodiaz.techwizards.domain.model.UserSession
import com.diegodiaz.techwizards.domain.repository.ScoreRepository
import com.diegodiaz.techwizards.data.remote.score.ScorePayload
import com.diegodiaz.techwizards.data.remote.score.LoginRequest


/**
 * Implementación de ScoreRepository basada en Retrofit (ScoreApi).
 *
 * @security
 * Reutiliza el token actual del [SessionManager] para firmar cada petición
 * mediante cabecera `Authorization: Bearer <token>`.
 */
class ScoreRepositoryRetrofit(
    private val scoreApi: ScoreApi,
    private val credentialsStore: CredentialsStore,
    private val sessionManager: SessionManager
) : ScoreRepository {
    private companion object {
        private const val MIN_ALIAS_LENGTH = 3
        private const val MAX_ALIAS_LENGTH = 30
        private const val MAX_SCORE = 100_000
    }
    private fun tokenOrNull(): String? =
        sessionManager.session.value?.token ?: credentialsStore.obtenerFirebaseToken()

    override suspend fun obtenerTopTen(): List<LeaderboardEntry> {
        val token = tokenOrNull()
        val bearer = token?.let { "Bearer $it" }
        val dtos = runCatching {
            scoreApi.fetchTopTen(bearerToken = bearer).items
        }.recoverCatching { error ->
            if (error is retrofit2.HttpException && error.code() == 404) {
                scoreApi.fetchTopTenLegacy(bearerToken = bearer)
            } else {
                throw error
            }
        }.getOrThrow()

        // DTO ya trae position opcional; forzamos una si viene null
        return dtos.mapIndexed { index, dto ->
            dto.toDomain(overridePosition = dto.position ?: (index + 1))
        }
    }

    override suspend fun publicarPuntuacion(session: UserSession, score: Int) {
        credentialsStore.guardarSesionAlias(session.token, session.alias)
        credentialsStore.guardarFirebaseToken(session.token)

        val sanitizedAlias = session.alias.trim()
        require(sanitizedAlias.isNotBlank()) { "alias vacío" }
        require(sanitizedAlias.length in MIN_ALIAS_LENGTH..MAX_ALIAS_LENGTH) {
            "alias fuera de rango"
        }
        require(score in 0..MAX_SCORE) { "score fuera de rango" }


        // ScoreApi SOLO acepta el body
        scoreApi.publishScore(
            bearerToken = "Bearer ${session.token}",
            ScorePayload(
                alias = sanitizedAlias,
                score = score
            )
        )
    }

    override suspend fun obtenerPremioComun(): CommonPrize {
        val token = tokenOrNull()
        val bearer = token?.let { "Bearer $it" }
        return scoreApi.fetchCommonPrize(bearerToken = bearer).toDomain()
    }

    override suspend fun actualizarPremioComun(
        session: UserSession,
        nuevoPremio: CommonPrize
    ): CommonPrize {
        credentialsStore.guardarSesionAlias(session.token, session.alias)
        credentialsStore.guardarFirebaseToken(session.token)

        // ScoreApi SOLO acepta el body
        return scoreApi.updatePrize(
            bearerToken = "Bearer ${session.token}",
            nuevoPremio.toRequestDto()
        ).toDomain()
    }

    override suspend fun autenticarAlias(alias: String): UserSession {
        val session = scoreApi.login(LoginRequest(alias)).toDomain()

        // Persistimos para futuras peticiones (si hay interceptor)
        credentialsStore.guardarSesionAlias(session.token, session.alias)
        credentialsStore.guardarFirebaseToken(session.token)

        return session
    }
}
