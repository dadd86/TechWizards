package com.diegodiaz.techwizards.data.remote.dto

/**
 * DTO para premios opcionales devueltos por el backend.
 *
 * Ejemplo de JSON:
 * {
 *   "name": "Poción",
 *   "description": "Rara"
 * }
 */
data class PrizeRemoteDto(
    val name: String,
    val description: String
)
