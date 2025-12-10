package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.AuthUser
import com.diegodiaz.techwizards.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Caso de uso: obtener el usuario autenticado en caché.
 */
class ObtenerUsuarioAutenticadoUseCase(
    private val authRepository: AuthRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(): Result<AuthUser?, AgentError> =
        withContext(ioDispatcher) {
            authRepository.getCachedUser()
        }
}
