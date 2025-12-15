package com.diegodiaz.techwizards.data.remote.match

import com.diegodiaz.techwizards.domain.model.Match
import com.diegodiaz.techwizards.domain.model.MatchEstado
import com.diegodiaz.techwizards.domain.model.MatchParticipant
import com.diegodiaz.techwizards.domain.model.MatchScore

/**
 * Mapeos entre DTOs de red y modelos de dominio.
 */
class MatchRemoteMapper {
    fun toDomain(dto: MatchDto): Match =
        Match(
            id = dto.id,
            lobbyId = dto.lobbyId,
            modo = dto.modo,
            estado = runCatching { MatchEstado.valueOf(dto.estado) }
                .getOrDefault(MatchEstado.PENDING),
            createdByNumero = dto.createdByNumero,
            createdAtMs = dto.createdAtMs,
            startedAtMs = dto.startedAtMs,
            finishedAtMs = dto.finishedAtMs
        )

    fun toDto(match: Match): MatchDto =
        MatchDto(
            id = match.id,
            lobbyId = match.lobbyId,
            modo = match.modo,
            estado = match.estado.name,
            createdByNumero = match.createdByNumero,
            createdAtMs = match.createdAtMs,
            startedAtMs = match.startedAtMs,
            finishedAtMs = match.finishedAtMs
        )

    fun toDomain(dto: MatchParticipantDto): MatchParticipant =
        MatchParticipant(
            matchId = dto.matchId,
            usuarioNumero = dto.usuarioNumero,
            rol = dto.rol,
            teamId = dto.teamId,
            joinedAtMs = dto.joinedAtMs,
            leftAtMs = dto.leftAtMs,
            score = dto.score
        )

    fun toDto(participant: MatchParticipant): MatchParticipantDto =
        MatchParticipantDto(
            matchId = participant.matchId,
            usuarioNumero = participant.usuarioNumero,
            rol = participant.rol,
            teamId = participant.teamId,
            joinedAtMs = participant.joinedAtMs,
            leftAtMs = participant.leftAtMs,
            score = participant.score
        )

    fun toDomain(dto: MatchScoreDto): MatchScore =
        MatchScore(
            matchId = dto.matchId,
            usuarioNumero = dto.usuarioNumero,
            score = dto.score
        )

    fun toDto(score: MatchScore): MatchScoreDto =
        MatchScoreDto(
            matchId = score.matchId,
            usuarioNumero = score.usuarioNumero,
            score = score.score
        )
}