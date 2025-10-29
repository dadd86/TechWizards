package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.MessageEntity
import com.diegodiaz.techwizards.domain.model.Message

/**
 * Convierte entre MessageEntity (BD local) y Message (modelo de dominio)
 */
fun MessageEntity.toDomain(): Message =
    Message(
        id = id,
        matchId = matchId,
        remitenteId = remitenteId,
        contenido = contenido,
        timestamp = timestamp
    )

fun Message.toEntity(): MessageEntity =
    MessageEntity(
        id = id,
        matchId = matchId,
        remitenteId = remitenteId,
        contenido = contenido,
        timestamp = timestamp
    )
