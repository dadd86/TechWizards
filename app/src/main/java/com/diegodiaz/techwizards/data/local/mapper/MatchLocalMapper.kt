package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.MatchEntity
import com.diegodiaz.techwizards.domain.model.Match
import com.diegodiaz.techwizards.domain.model.MatchEstado

/**
 * Mapeo entre MatchEntity y Match.
 *
 * @security
 * - Controla el estado válido mediante el enum del dominio.
 */
fun MatchEntity.toDomain(): Match =
    Match(
        id = id,
        lobbyId = lobbyId,
        modo = modo,
        estado = MatchEstado.valueOf(estado),
        createdByNumero = createdByNumero,
        createdAtMs = createdAtMs,
        startedAtMs = startedAtMs,
        finishedAtMs = finishedAtMs,
    )

fun Match.toEntity(): MatchEntity =
    MatchEntity(
        id = id,
        lobbyId = lobbyId,
        modo = modo,
        estado = estado.name,
        createdByNumero = createdByNumero,
        createdAtMs = createdAtMs,
        startedAtMs = startedAtMs,
        finishedAtMs = finishedAtMs,
    )
