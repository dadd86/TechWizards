package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.MatchEntity
import com.diegodiaz.techwizards.domain.model.Match

fun MatchEntity.toDomain() = Match(
    id = id,
    lobbyId = lobbyId,
    status = Match.Status.valueOf(status),
    inicioEn = inicioEn,
    finEn = finEn
)

fun Match.toEntity() = MatchEntity(
    id = id,
    lobbyId = lobbyId,
    status = status.name,
    inicioEn = inicioEn,
    finEn = finEn
)

