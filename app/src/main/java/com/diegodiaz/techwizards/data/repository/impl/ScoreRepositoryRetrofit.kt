package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.credenciales.CredentialsStore
import com.diegodiaz.techwizards.data.remote.score.ScoreApi
import com.diegodiaz.techwizards.data.remote.score.toDomain
import com.diegodiaz.techwizards.data.remote.score.toDto
import com.diegodiaz.techwizards.domain.model.CommonPrize
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry
import com.diegodiaz.techwizards.domain.model.UserSession
import com.diegodiaz.techwizards.domain.repository.ScoreRepository

/**
 * Implementación de ScoreRepository basada en Retrofit (ScoreApi).
 *
 * IMPORTANTE:
 * - ScoreApi.publishScore(...) y updatePrize(...) NO aceptan @Header bearer.
 * - La autorización debe ir por Interceptor (OkHttp) o por configuración del backend.
 */
class ScoreRepositoryRetrofit(
    private val scoreApi: ScoreApi,
    private val credentialsStore: CredentialsStore
) : ScoreRepository {

    override suspend fun obtenerTopTen(): List<LeaderboardEntry> {
        val dtos = scoreApi.fetchTopTen()

        // DTO ya trae position opcional; forzamos una si viene null
        return dtos.mapIndexed { index, dto ->
            dto.toDomain(overridePosition = dto.position ?: (index + 1))
        }
    }

    override suspend fun publicarPuntuacion(session: UserSession, score: Int) {
        // Si vuestro backend usa token por Interceptor, guardamos el token aquí para que lo inyecte.
        // (Esto no rompe nada aunque no se use)
        credentialsStore.guardarSesionAlias(session.token, session.alias)
        credentialsStore.guardarFirebaseToken(session.token)

        // ScoreApi SOLO acepta el body
        scoreApi.publishScore(
            ScorePayload(
                alias = session.alias,
                score = score
            )
        )
    }

    override suspend fun obtenerPremioComun(): CommonPrize {
        return scoreApi.fetchPrize().toDomain()
    }

    override suspend fun actualizarPremioComun(
        session: UserSession,
        nuevoPremio: CommonPrize
    ): CommonPrize {
        credentialsStore.guardarSesionAlias(session.token, session.alias)
        credentialsStore.guardarFirebaseToken(session.token)

        // ScoreApi SOLO acepta el body
        return scoreApi.updatePrize(
            nuevoPremio.toDto()
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
