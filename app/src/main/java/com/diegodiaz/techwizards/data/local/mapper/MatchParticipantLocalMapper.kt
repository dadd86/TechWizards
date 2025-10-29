package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.MatchParticipantEntity
import com.diegodiaz.techwizards.domain.model.MatchParticipant

fun MatchParticipantEntity.toDomain() = MatchParticipant(
    id = id,
    matchId = matchId,
    userId = userId,
    apodo = apodo,
    joinedAt = joinedAt,
    esGanador = esGanador
)

fun MatchParticipant.toEntity() = MatchParticipantEntity(
    id = id,
    matchId = matchId,
    userId = userId,
    apodo = apodo,
    joinedAt = joinedAt,
    esGanador = esGanador
)
