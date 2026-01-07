package com.diegodiaz.techwizards.domain.model

/**
 * Sesión autenticada utilizada para autorizar peticiones remotas.
 *
 * @property token JWT devuelto por Firebase Authentication.
 * @property alias Alias público del jugador.
 * @property isAdmin Indica si el backend reporta permisos administrativos.
 * @property backendToken Token del backend para endpoints internos (no sustituye al Firebase ID token).
 * @security No expone el token ni lo registra en logs.
 */
data class UserSession(
    val token: String,
    val alias: String,
    val isAdmin: Boolean = false,
    val backendToken: String? = null

) {
    val bearerToken: String
    get() = "Bearer $token"
}