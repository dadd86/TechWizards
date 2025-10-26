package com.diegodiaz.techwizards.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa tareas pendientes de sincronización con el servidor
 * (por ejemplo, partidas que aún no se han subido).
 */
@Entity(tableName = "outbox")
data class OutboxEntity(
    @PrimaryKey val id: String,       // Identificador único
    val tipo: String,                 // Tipo de evento (ej: "partida", "monedero")
    val payload: String,              // Contenido en JSON o texto plano
    val timestamp: Long               // Cuándo se generó
)
