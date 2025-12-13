package com.diegodiaz.techwizards.domain.model

/**
 * Sesión autenticada utilizada para autorizar peticiones remotas.
 *
 */
data class UserSession(
    val token: String,
    val alias: String
) {
    val bearerToken: String
    get() = "Bearer $token"
}
