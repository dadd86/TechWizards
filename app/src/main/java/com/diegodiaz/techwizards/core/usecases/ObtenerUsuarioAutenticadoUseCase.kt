package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.domain.model.AuthUser
import com.diegodiaz.techwizards.domain.repository.AuthRepository

class ObtenerUsuarioAutenticadoUseCase (
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): AuthUser? =
        authRepository.obtenerUsuario()
}