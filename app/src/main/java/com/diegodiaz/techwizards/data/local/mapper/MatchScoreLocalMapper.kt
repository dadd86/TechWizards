package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.MatchScoreEntity
import com.diegodiaz.techwizards.domain.model.MatchScore

fun MatchScoreEntity.toDomain() = MatchScore(
    id = id,
    matchId = matchId,
    participantId = participantId,
    puntos = puntos
)

fun MatchScore.toEntity() = MatchScoreEntity(
    id = id,
    matchId = matchId,
    participantId = participantId,
    puntos = puntos
)
