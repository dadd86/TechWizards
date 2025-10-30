package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.MatchParticipantEntity
import com.diegodiaz.techwizards.domain.model.MatchParticipant

/**
 * Mapeo entre participantes persistidos y dominio.
 *
 * @security
 * - Mantiene únicamente referencias internas sin PII.
 */
fun MatchParticipantEntity.toDomain(): MatchParticipant =
    MatchParticipant(
        matchId = matchId,
        usuarioNumero = usuarioNumero,
        rol = rol,
        teamId = teamId,
        joinedAtMs = joinedAtMs,
        leftAtMs = leftAtMs,
        score = score,
    )

fun MatchParticipant.toEntity(): MatchParticipantEntity =
    MatchParticipantEntity(
        matchId = matchId,
        usuarioNumero = usuarioNumero,
        rol = rol,
        teamId = teamId,
        joinedAtMs = joinedAtMs,
        leftAtMs = leftAtMs,
        score = score,
    )