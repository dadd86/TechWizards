package com.diegodiaz.techwizards.domain.model

/**
 * Sesión autenticada utilizada para autorizar peticiones remotas.
 *
 * Esta clase representa el estado de autenticación actual del usuario
 * y expone un token de acceso que puede usarse en las llamadas al backend.
 */
data class UserSession(
    val userId: String,
    val displayName: String,
    val email: String? = null,
    val accessToken: String,
    val expiresAtMillis: Long? = null) {
    val bearerToken: String
        get() = "Bearer $accessToken"
}