package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.domain.model.AuthUser
import com.diegodiaz.techwizards.domain.repository.AuthRepository

class IniciarSesionConGoogleUseCase (
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): AuthUser =
        authRepository.iniciarSesionConGoogle(idToken)
}