package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.core.SessionManager
import com.diegodiaz.techwizards.credenciales.CredentialsStore
import com.diegodiaz.techwizards.data.remote.score.LoginRequest
import com.diegodiaz.techwizards.data.remote.score.ScoreApi
import com.diegodiaz.techwizards.data.remote.score.ScorePayload
import com.diegodiaz.techwizards.data.remote.score.toDomain
import com.diegodiaz.techwizards.data.remote.score.toRequestDto
import com.diegodiaz.techwizards.domain.model.CommonPrize
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry
import com.diegodiaz.techwizards.domain.model.UserSession
import com.diegodiaz.techwizards.domain.repository.ScoreRepository
import retrofit2.HttpException
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
        val bearer = tokenOrNull()?.let { "Bearer $it" }
        return runCatching {
            scoreApi.fetchTopTen(bearerToken = bearer)
        }.map { response ->
            response.items.mapIndexed { index, item ->
                item.toDomain(position = index + 1)
            }
        }.getOrElse { error ->
            val httpError = error as? HttpException
            if (httpError != null && httpError.code() in setOf(404, 405)) {
                val dtos = scoreApi.fetchTopTenLegacy(bearerToken = bearer)
                return@getOrElse dtos.mapIndexed { index, dto ->
                    dto.toDomain().copy(
                        position = dto.position ?: (index + 1)
                    )
                }
            }
            throw error
        }
    }

    override suspend fun publicarPuntuacion(session: UserSession, score: Int) {
        credentialsStore.guardarSesionAlias(session.token, session.alias)
        credentialsStore.guardarFirebaseToken(session.token)

        val sanitizedAlias = session.alias.trim()
        require(sanitizedAlias.isNotBlank()) { "alias vacío" }
        require(sanitizedAlias.length in MIN_ALIAS_LENGTH..MAX_ALIAS_LENGTH) { "alias fuera de rango" }

        // score aquí es DELTA (puede ser negativo)
        require(score in -MAX_SCORE..MAX_SCORE) { "score fuera de rango" }


    }

    override suspend fun obtenerPremioComun(): CommonPrize {
        val bearer = tokenOrNull()?.let { "Bearer $it" }
        return scoreApi.fetchCommonPrize(bearerToken = bearer).toDomain()
    }

    override suspend fun actualizarPremioComun(session: UserSession, nuevoPremio: CommonPrize): CommonPrize {
        credentialsStore.guardarSesionAlias(session.token, session.alias)
        credentialsStore.guardarFirebaseToken(session.token)

        return scoreApi.updatePrize(
            bearerToken = "Bearer ${session.token}",
            request = nuevoPremio.toRequestDto()
        ).toDomain()
    }

    override suspend fun autenticarAlias(alias: String): UserSession {
        val firebaseToken = tokenOrNull()
            ?: error("No hay Firebase token. Debes autenticarte con Firebase antes de llamar /login")

        val session = scoreApi.login(
            bearerToken = "Bearer $firebaseToken",
            request = LoginRequest(alias)
        ).toDomain()

        credentialsStore.guardarSesionAlias(session.token, session.alias)
        credentialsStore.guardarFirebaseToken(session.token)

        return session
    }
}
