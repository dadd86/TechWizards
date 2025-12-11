package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.domain.model.AuthUser
import com.diegodiaz.techwizards.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class ObservarUsuarioAutenticadoUseCase (
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<AuthUser?> =
        authRepository.observarUsuario()
}