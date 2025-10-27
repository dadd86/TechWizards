package com.diegodiaz.techwizards.data.local.mapper


import com.diegodiaz.techwizards.data.local.entity.EventoEntity
import com.diegodiaz.techwizards.domain.model.Evento


// ⚠ Este mapper se usa actualmente para la entidad Evento (no MatchEvent),
// ya que el modelo de partida no implementa eventos separados.

/**
 * Conversión directa entre EventoEntity (Room) y Evento (modelo de dominio).
 */
fun EventoEntity.toDomain(): Evento =
    Evento(
        id = id,
        nombre = nombre,
        descripcion = descripcion,
        fechaInicio = fechaInicio,
        fechaFin = fechaFin,
        completado = completado
    )

fun Evento.toEntity(): EventoEntity =
    EventoEntity(
        id = id,
        nombre = nombre,
        descripcion = descripcion,
        fechaInicio = fechaInicio,
        fechaFin = fechaFin,
        completado = completado
    )
