package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.MessageEntity
import com.diegodiaz.techwizards.domain.model.Message

/**
 * Conversión entre mensajes persistidos y de dominio.
 *
 * @security
 * - El contenido debe llegar sanitizado desde UI/UseCases antes de persistir.
 */
fun MessageEntity.toDomain(): Message =
    Message(
        id = id,
        matchId = matchId,
        senderNumero = senderNumero,
        text = text,
        createdAtMs = createdAtMs,
    )

fun Message.toEntity(): MessageEntity =
    MessageEntity(
        id = id,
        matchId = matchId,
        senderNumero = senderNumero,
        text = text,
        createdAtMs = createdAtMs,
    )