package com.diegodiaz.techwizards.ui.controller

import com.diegodiaz.techwizards.domain.model.AuthUser

data class AuthState(
    val usuario: AuthUser? = null,
    val cargando: Boolean = false,
    val error: String? = null
)