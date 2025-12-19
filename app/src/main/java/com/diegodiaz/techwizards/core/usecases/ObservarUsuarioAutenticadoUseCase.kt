package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.domain.model.AuthUser
import com.diegodiaz.techwizards.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

/**
 * Caso de uso: observar cambios en la sesión autenticada.
 */
class ObservarUsuarioAutenticadoUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<AuthUser?> = authRepository.observeUser()
}
