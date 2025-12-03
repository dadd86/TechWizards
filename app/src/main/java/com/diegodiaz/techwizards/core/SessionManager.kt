package com.diegodiaz.techwizards.core

import com.diegodiaz.techwizards.domain.model.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * Gestiona la sesión autenticada de la app de forma in-memory.
 *
 * @security
 * No persiste el token en disco y evita exponerlo en logs.
 */
class SessionManager {

    private val _session = MutableStateFlow<UserSession?>(null)
    val session: StateFlow<UserSession?> = _session

    fun setSession(session: UserSession) {
        _session.value = session
    }

    fun clearSession() {
        _session.update { null }
    }
}