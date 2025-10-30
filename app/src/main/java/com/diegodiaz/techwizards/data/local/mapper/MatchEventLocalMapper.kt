package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.MatchEventEntity
import com.diegodiaz.techwizards.domain.model.MatchEvent

/**
 * Mapeo entre evento persistido y dominio.
 *
 * @security
 * - Garantiza consistencia del payload al trasladarlo entre capas.
 */
fun MatchEventEntity.toDomain(): MatchEvent =
    MatchEvent(
        id = id,
        matchId = matchId,
        seq = seq,
        type = type,
        actorNumero = actorNumero,
        payloadJson = payloadJson,
        createdAtMs = createdAtMs,
    )

fun MatchEvent.toEntity(): MatchEventEntity =
    MatchEventEntity(
        id = id,
        matchId = matchId,
        seq = seq,
        type = type,
        actorNumero = actorNumero,
        payloadJson = payloadJson,
        createdAtMs = createdAtMs,
    )

