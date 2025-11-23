package com.diegodiaz.techwizards.domain.model

/**
 * Modelo de dominio para la ubicación del jugador al ganar.
 */
data class VictoryLocation(
    val id: Long? = null,
    val latitude: Double,
    val longitude: Double,
    val accuracyMetres: Double? = null,
    val capturedAtMs: Long
)
