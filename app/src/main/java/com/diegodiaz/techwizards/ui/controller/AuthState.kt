package com.diegodiaz.techwizards.ui.controller

import com.diegodiaz.techwizards.domain.model.AuthUser

/**
 * Estado observable de autenticación para la UI.
 */
data class AuthState(
    val usuario: AuthUser? = null,
    val cargando: Boolean = false,
    val error: String? = null
)
