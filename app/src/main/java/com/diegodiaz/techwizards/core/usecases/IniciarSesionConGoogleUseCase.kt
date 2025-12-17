package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.core.SessionManager
import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.credenciales.CredentialsStore
import com.diegodiaz.techwizards.domain.model.AuthUser
import com.diegodiaz.techwizards.domain.model.UserSession
import com.diegodiaz.techwizards.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Caso de uso: iniciar sesión con Google usando un idToken.
 */
class IniciarSesionConGoogleUseCase(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    private val credentialsStore: CredentialsStore,
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
                            val alias = signInResult.value.displayName
                                ?: signInResult.value.email
                                ?: "Jugador"
                            val session = UserSession(token = tokenResult.value, alias = alias)

                            sessionManager.setSession(session)
                            credentialsStore.guardarFirebaseToken(session.token)
                            credentialsStore.guardarSesionAlias(session.token, session.alias)

                            signInResult
                        }
                    }
                }
            }

        }
}
