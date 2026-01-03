package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.core.SessionManager
import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.credenciales.CredentialsStore
import com.diegodiaz.techwizards.domain.model.AuthUser
import com.diegodiaz.techwizards.domain.model.UserSession
import com.diegodiaz.techwizards.domain.repository.AuthRepository
import com.diegodiaz.techwizards.data.remote.score.LoginRequest
import com.diegodiaz.techwizards.data.remote.score.ScoreApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IniciarSesionConGoogleUseCase(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    private val credentialsStore: CredentialsStore,
    private val scoreApi: ScoreApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(idToken: String): Result<AuthUser, AgentError> =
        withContext(ioDispatcher) {

            when (val signInResult = authRepository.signInWithGoogle(idToken)) {
                is Result.Err -> signInResult
                is Result.Ok -> {
                    when (val tokenResult = authRepository.fetchIdToken(forceRefresh = true)) {
                        is Result.Err -> tokenResult
                        is Result.Ok -> {
                            val firebaseToken = tokenResult.value
                            val alias = signInResult.value.displayName
                                ?: signInResult.value.email
                                ?: "Jugador"

                            // ✅ 1) Login backend para conseguir backendToken
                            val backendSession = try {
                                scoreApi.login(
                                    bearerToken = "Bearer $firebaseToken",
                                    request = LoginRequest(alias = alias)
                                )
                            } catch (e: Exception) {
                                return@withContext Result.Err(AgentError.Unknown(e))
                            }

                            // ✅ 2) Guardar sesión completa (firebaseToken + backendToken)
                            val session = UserSession(
                                token = firebaseToken,
                                alias = backendSession.alias,
                                backendToken = backendSession.token,
                                isAdmin = backendSession.isAdmin
                            )

                            sessionManager.setSession(session)
                            credentialsStore.guardarFirebaseToken(session.token)

                            // (si guardas alias en CredentialsStore, OK, pero esta clave tuya parece rara)
                            // credentialsStore.guardarSesionAlias(session.token, session.alias)

                            signInResult
                        }
                    }
                }
            }
        }
}