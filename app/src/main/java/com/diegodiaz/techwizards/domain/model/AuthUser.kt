package com.diegodiaz.techwizards.domain.model

/**
 * Perfil mínimo autenticado en Firebase/Google.
 *
 * Vive en la capa de dominio para no acoplar la UI a clases de Firebase.
 */
data class AuthUser(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?
)

