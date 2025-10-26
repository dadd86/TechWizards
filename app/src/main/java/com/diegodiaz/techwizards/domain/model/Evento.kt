package com.diegodiaz.techwizards.domain.model

/**
 * Representa un evento o misión dentro del juego.
 * Es el modelo del dominio (sin dependencias de Room).
 */
data class Evento(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val fechaInicio: Long,
    val fechaFin: Long,
    val completado: Boolean = false
)
