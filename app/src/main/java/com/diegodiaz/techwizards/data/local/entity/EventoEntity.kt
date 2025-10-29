package com.diegodiaz.techwizards.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un evento del juego (misiones, desafíos, torneos, etc.).
 */
@Entity(tableName = "evento")
data class EventoEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val descripcion: String,
    val fechaInicio: Long,   // epoch millis
    val fechaFin: Long,      // epoch millis
    val completado: Boolean = false
)
