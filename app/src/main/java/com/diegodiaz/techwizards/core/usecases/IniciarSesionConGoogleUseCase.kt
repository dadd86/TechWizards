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

                            val session = UserSession(
                                token = firebaseToken,
                                alias = alias,
                                backendToken = null,
                                isAdmin = false
                            )

                            sessionManager.setSession(session)
                            credentialsStore.guardarFirebaseToken(session.token)

                            val backendSession = runCatching {
                                scoreApi.login(
                                    bearerToken = "Bearer $firebaseToken",
                                    request = LoginRequest(alias = alias)
                                )
                            }.getOrNull()

                            if (backendSession != null) {
                                sessionManager.setSession(
                                    session.copy(
                                        alias = backendSession.alias,
                                        backendToken = backendSession.token,
                                        isAdmin = backendSession.isAdmin
                                    )
                                )
                            }

                            signInResult
                        }
                    }
                }
            }
        }
}