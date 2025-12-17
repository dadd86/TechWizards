package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.core.SessionManager
import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.credenciales.CredentialsStore
import com.diegodiaz.techwizards.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Caso de uso: cerrar sesión y limpiar datos locales.
 */
class CerrarSesionUseCase(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    private val credentialsStore: CredentialsStore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(): Result<Unit, AgentError> =
        withContext(ioDispatcher) {
            when (val result = authRepository.signOut()) {
                is Result.Err -> result
                is Result.Ok -> {
                    sessionManager.clearSession()
                    credentialsStore.guardarFirebaseToken(null)
                    credentialsStore.guardarSesionAlias(null, null)
                    result
                }
            }
        }
}
