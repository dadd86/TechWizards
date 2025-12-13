package com.diegodiaz.techwizards.data.remote.mapper

import com.diegodiaz.techwizards.data.remote.dto.ScoreRemoteDto
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry

/**
 * Mapper entre DTO remoto y modelo de dominio para el ranking.
 *
 * IMPORTANTE: Es una clase porque ServiceLocator instancia ScoreRemoteMapper().
 */
class ScoreRemoteMapper {

    fun toDomain(dto: ScoreRemoteDto, position: Int): LeaderboardEntry {
        return LeaderboardEntry(
            id = dto.id,
            alias = dto.player,
            score = dto.points,
            position = dto.position ?: position,
            prizeName = dto.prize?.name,
            prizeDescription = dto.prize?.description
        )
    }
}
