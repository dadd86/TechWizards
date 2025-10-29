package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.EventoEntity
import com.diegodiaz.techwizards.domain.model.Evento

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