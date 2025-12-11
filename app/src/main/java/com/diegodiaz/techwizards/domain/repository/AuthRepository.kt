package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun iniciarSesionConGoogle(idToken: String): AuthUser
    suspend fun cerrarSesion()
    suspend fun obtenerUsuario(): AuthUser?
    fun observarUsuario(): Flow<AuthUser?>
}