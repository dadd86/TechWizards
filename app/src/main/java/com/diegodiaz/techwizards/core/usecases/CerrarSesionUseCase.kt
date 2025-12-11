package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.domain.repository.AuthRepository

class CerrarSesionUseCase (
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.cerrarSesion()
    }
}