package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

/**
 * Fuente de autenticación y persistencia mínima de perfil.
 *
 * - Realiza inicio de sesión con idToken de Google.
 * - Cierra la sesión y limpia datos locales.
 * - Recupera el usuario autenticado en caché.
 * - Expone cambios de sesión autenticada.
 */
interface AuthRepository {

    suspend fun signInWithGoogle(idToken: String): Result<AuthUser, AgentError>
    suspend fun fetchIdToken(forceRefresh: Boolean = false): Result<String, AgentError>

    suspend fun signOut(): Result<Unit, AgentError>

    suspend fun getCachedUser(): Result<AuthUser?, AgentError>

    fun observeUser(): Flow<AuthUser?>
}


