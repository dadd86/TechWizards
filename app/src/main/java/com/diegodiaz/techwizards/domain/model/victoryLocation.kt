package com.diegodiaz.techwizards.domain.model

/**
 * Modelo de dominio para la ubicación del jugador al ganar.
 */
data class victoryLocation(
    val id: Long? = null,
    val matchId: Long? = null,
    val latitude: Double,
    val longitude: Double,
    val timestampMillis: Long
)
