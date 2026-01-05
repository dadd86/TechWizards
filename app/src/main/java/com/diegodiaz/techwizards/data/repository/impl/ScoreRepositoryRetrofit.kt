package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.core.SessionManager
import com.diegodiaz.techwizards.credenciales.CredentialsStore
import com.diegodiaz.techwizards.data.remote.firestore.FirestoreCollectionSelectorDto
import com.diegodiaz.techwizards.data.remote.firestore.FirestoreFieldReferenceDto
import com.diegodiaz.techwizards.data.remote.firestore.FirestoreLeaderboardApi
import com.diegodiaz.techwizards.data.remote.firestore.FirestoreLeaderboardSdkDataSource
import com.diegodiaz.techwizards.data.remote.firestore.FirestoreOrderByDto
import com.diegodiaz.techwizards.data.remote.firestore.FirestoreRunQueryRequestDto
import com.diegodiaz.techwizards.data.remote.firestore.FirestoreStructuredQueryDto
import com.diegodiaz.techwizards.data.remote.firestore.FirestorePlayersApi
import com.diegodiaz.techwizards.data.remote.firestore.winsOrNull
import com.diegodiaz.techwizards.data.remote.firestore.toLeaderboardEntry
import com.diegodiaz.techwizards.data.remote.prize.PremioComunFirestoreDataSource
import com.diegodiaz.techwizards.data.remote.firestore.PrizeCommonFirebaseDataSource
import com.diegodiaz.techwizards.data.remote.score.LoginRequest
import com.diegodiaz.techwizards.data.remote.score.ScoreApi
import com.diegodiaz.techwizards.data.remote.score.ScorePayload
import com.diegodiaz.techwizards.data.remote.score.toDomain
import com.diegodiaz.techwizards.data.remote.score.toRequestDto
import com.diegodiaz.techwizards.domain.model.CommonPrize
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry
import com.diegodiaz.techwizards.domain.model.UserSession
import com.diegodiaz.techwizards.domain.repository.ScoreRepository
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import com.google.gson.JsonSyntaxException
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import retrofit2.HttpException
import com.diegodiaz.techwizards.data.remote.score.PrizeIncrementRequestDto
import com.diegodiaz.techwizards.data.remote.score.PrizeClaimRequestDto
import com.diegodiaz.techwizards.data.remote.score.PrizeClaimResponseDto
import com.google.firebase.auth.FirebaseAuth



class ScoreRepositoryRetrofit(
    private val scoreApi: ScoreApi,
    private val credentialsStore: CredentialsStore,
    private val sessionManager: SessionManager,
    private val firebaseAuth: FirebaseAuth,
    private val prizeCommonDataSource: PrizeCommonFirebaseDataSource,
    private val premioComunDataSource: PremioComunFirestoreDataSource,
    private val firestorePlayersApi: FirestorePlayersApi? = null,
    private val firestoreLeaderboardApi: FirestoreLeaderboardApi? = null,
    private val firestoreLeaderboardSdkDataSource: FirestoreLeaderboardSdkDataSource? = null
) : ScoreRepository {

    private companion object {
        private const val MIN_ALIAS_LENGTH = 3
        private const val MAX_ALIAS_LENGTH = 30
        private const val MAX_SCORE = 100_000
    }

    private fun tokenOrNull(): String? {
        val session = sessionManager.session.value
        val sessionToken = session?.token?.trim()
        val storedToken = credentialsStore.obtenerFirebaseToken()?.trim()
        val validSessionToken = isFirebaseToken(sessionToken, session?.backendToken)
        val validStoredToken = isFirebaseToken(storedToken, session?.backendToken)
        return when {
            validSessionToken -> sessionToken
            validStoredToken -> storedToken
            else -> null
        }
    }

    private fun isFirebaseToken(token: String?, backendToken: String?): Boolean {
        val normalizedToken = token?.trim().orEmpty()
        if (normalizedToken.isEmpty()) return false
        if (normalizedToken.startsWith("local-")) return false
        if (!isLikelyJwt(normalizedToken)) return false
        if (!backendToken.isNullOrBlank() && backendToken == normalizedToken) return false
        return true
    }

    private fun isLikelyJwt(token: String): Boolean {
        val parts = token.split('.')
        return parts.size == 3 && parts.all { it.isNotBlank() }
    }
    private fun requireFirebaseUid(): String {
        val uid = firebaseAuth.currentUser?.uid?.trim()
        require(!uid.isNullOrBlank()) { "firebaseUid vacío" }
        return uid
    }



    private fun requireFirebaseToken(session: UserSession): String {
        val token = session.token.trim()
        check(isFirebaseToken(token, session.backendToken)) { "Sesión Firebase inválida" }
        return token
    }


    override suspend fun obtenerTopTen(): List<LeaderboardEntry> {
        val sdkSource = firestoreLeaderboardSdkDataSource
        if (sdkSource != null) {
            val sdkResult = runCatching { sdkSource.obtenerTopTen() }
                .onFailure { error ->
                    DecentralizedLogger.e(
                        "ScoreRepositoryRetrofit",
                        "Fallo lectura SDK Firestore, usando fallback remoto",
                        error
                    )
                }
                .getOrNull()
            if (!sdkResult.isNullOrEmpty()) {
                return sdkResult
            }
        }
        val bearer = tokenOrNull()?.let { "Bearer $it" }
        val base = try {
            scoreApi.fetchTopTen(bearerToken = bearer).items.mapIndexed { index, item ->
                item.toDomain(position = index + 1)
            }
        } catch (error: Exception) {
            val httpError = error as? HttpException

            val isParsingError = error is JsonDataException ||
                    error is JsonEncodingException ||
                    error is JsonSyntaxException
            if ((httpError != null && httpError.code() in setOf(404, 405)) || isParsingError) {
                val dtos = scoreApi.fetchTopTenLegacy(bearerToken = bearer)
                return completarVictorias(
                    dtos.mapIndexed { index, dto ->
                        dto.toDomain().copy(
                            position = dto.position ?: (index + 1)
                        )
                    }
                )
            }
            val firestoreFallback = obtenerTopTenDesdeFirestore()
            if (firestoreFallback.isNotEmpty()) return firestoreFallback
            throw error
        }
        val enriquecido = completarVictorias(base)
        if (enriquecido.isNotEmpty()) return enriquecido
        val firestoreFallback = obtenerTopTenDesdeFirestore()
        return if (firestoreFallback.isNotEmpty()) firestoreFallback else enriquecido
    }

    override suspend fun publicarPuntuacion(session: UserSession, score: Int) {
        val firebaseToken = requireFirebaseToken(session)
        credentialsStore.guardarSesionAlias(firebaseToken, session.alias)
        credentialsStore.guardarFirebaseToken(firebaseToken)

        val sanitizedAlias = session.alias.trim()
        require(sanitizedAlias.isNotBlank()) { "alias vacío" }
        require(sanitizedAlias.length in MIN_ALIAS_LENGTH..MAX_ALIAS_LENGTH) { "alias fuera de rango" }

        // score aquí es DELTA (puede ser negativo)
        require(score in -MAX_SCORE..MAX_SCORE) { "score fuera de rango" }

        scoreApi.publicarScore(
            bearerToken = "Bearer $firebaseToken",
            score = ScorePayload(alias = sanitizedAlias, deltaMonedas = score)
        )
    }

    override suspend fun obtenerPremioComun(): CommonPrize {
        return premioComunDataSource.obtenerPremioComun()
    }

    override suspend fun actualizarPremioComun(session: UserSession, nuevoPremio: CommonPrize): CommonPrize {
        val firebaseToken = requireFirebaseToken(session)
        credentialsStore.guardarSesionAlias(firebaseToken, session.alias)
        credentialsStore.guardarFirebaseToken(firebaseToken)
        return premioComunDataSource.actualizarPremioComun(nuevoPremio)
    }

    override suspend fun autenticarAlias(alias: String): UserSession {
        val firebaseToken = tokenOrNull()
            ?: error("No hay Firebase token. Debes autenticarte con Firebase antes de llamar /login")

        val backendSession = scoreApi.login(
            bearerToken = "Bearer $firebaseToken",
            request = LoginRequest(alias)
        )

        val session = UserSession(
            token = firebaseToken,
            alias = backendSession.alias,
            backendToken = backendSession.token,
            isAdmin = backendSession.isAdmin
        )

        sessionManager.setSession(session)

        credentialsStore.guardarSesionAlias(firebaseToken, session.alias)
        credentialsStore.guardarFirebaseToken(firebaseToken)

        return session
    }
    private suspend fun completarVictorias(
        topTen: List<LeaderboardEntry>
    ): List<LeaderboardEntry> {
        val api = firestorePlayersApi ?: return topTen
        if (tokenOrNull().isNullOrBlank()) return topTen
        return topTen.map { entry ->
            if (entry.wins != null) return@map entry
            val userId = entry.id ?: return@map entry
            val wins = runCatching {
                api.obtenerJugador(userId).winsOrNull()
            }.getOrNull()
            if (wins == null) {
                entry
            } else {
                entry.copy(wins = wins)
            }
        }
    }

    private suspend fun obtenerTopTenDesdeFirestore(): List<LeaderboardEntry> {
        val api = firestoreLeaderboardApi ?: return emptyList()
        if (tokenOrNull().isNullOrBlank()) return emptyList()
        val request = FirestoreRunQueryRequestDto(
            structuredQuery = FirestoreStructuredQueryDto(
                from = listOf(FirestoreCollectionSelectorDto(collectionId = "players")),
                orderBy = listOf(
                    FirestoreOrderByDto(
                        field = FirestoreFieldReferenceDto(fieldPath = "wins"),
                        direction = "DESCENDING"
                    )
                ),
                limit = 10
            )
        )
        return api.obtenerTopTen(request)
            .mapNotNullIndexed { index, response ->
                response.toLeaderboardEntry(position = index + 1)
            }
    }

    private inline fun <T, R : Any> List<T>.mapNotNullIndexed(
        transform: (index: Int, value: T) -> R?
    ): List<R> {
        val result = ArrayList<R>(size)
        forEachIndexed { index, value ->
            transform(index, value)?.let(result::add)
        }
        return result
    }


    override suspend fun incrementarPremioComun(session: UserSession, delta: Int): CommonPrize {
        val firebaseToken = requireFirebaseToken(session)
        credentialsStore.guardarSesionAlias(firebaseToken, session.alias)
        credentialsStore.guardarFirebaseToken(firebaseToken)
        return premioComunDataSource.incrementarPremioComun(delta)
    }

    override suspend fun reclamarPremioComun(session: UserSession, claimId: String): Int {
        val firebaseToken = requireFirebaseToken(session)
        credentialsStore.guardarSesionAlias(firebaseToken, session.alias)
        credentialsStore.guardarFirebaseToken(firebaseToken)
        val uid = requireFirebaseUid()
        return premioComunDataSource.reclamarPremioComun(
            firebaseUid = uid,
            alias = session.alias,
            claimId = claimId
        )
    }

    private fun requireFirebaseUser(): String {
        val uid = firebaseAuth.currentUser?.uid?.trim()
        require(!uid.isNullOrBlank()) { "Usuario no autenticado" }
        return uid
    }

}
