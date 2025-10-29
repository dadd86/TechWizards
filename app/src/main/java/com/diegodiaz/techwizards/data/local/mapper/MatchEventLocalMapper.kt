package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.MatchEventEntity
import com.diegodiaz.techwizards.domain.model.MatchEvent

fun MatchEventEntity.toDomain() = MatchEvent(
    id = id,
    matchId = matchId,
    tipo = tipo,
    timestamp = timestamp,
    actorParticipantId = actorParticipantId,
    payload = payload
)

fun MatchEvent.toEntity() = MatchEventEntity(
    id = id,
    matchId = matchId,
    tipo = tipo,
    timestamp = timestamp,
    actorParticipantId = actorParticipantId,
    payload = payload
)


