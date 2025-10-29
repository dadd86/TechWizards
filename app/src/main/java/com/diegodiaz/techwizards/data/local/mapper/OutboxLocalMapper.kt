package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.OutboxEntity
import com.diegodiaz.techwizards.domain.model.Outbox

fun OutboxEntity.toDomain() = Outbox(
    id = id,
    tipo = tipo,
    payload = payload,
    creadoEn = creadoEn,
    entregado = entregado,
    reintentos = reintentos
)

fun Outbox.toEntity() = OutboxEntity(
    id = id, // si quieres que lo autogenere Room al insertar, pasa 0 en altas nuevas
    tipo = tipo,
    payload = payload,
    creadoEn = creadoEn,
    entregado = entregado,
    reintentos = reintentos
)
