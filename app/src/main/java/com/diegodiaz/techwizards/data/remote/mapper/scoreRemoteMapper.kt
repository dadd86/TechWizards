package com.diegodiaz.techwizards.data.remote.mapper

import com.diegodiaz.techwizards.data.remote.dto.ScoreRemoteDto
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry

/**
 * Funciones de mapeo entre DTOs remotos y modelos de dominio.
 *
 * Convierte las respuestas del backend (ScoreRemoteDto) en
 * entradas de ranking de dominio (LeaderboardEntry).
 */

/**
 * Mapea un único ScoreRemoteDto a una entrada de leaderboard de dominio.
 */
fun ScoreRemoteDto.toDomain(): LeaderboardEntry =
    LeaderboardEntry(
        id = id,
        alias = player,
        score = points,
        position = position,
        prizeName = prize?.name,
        prizeDescription = prize?.description
    )

/**
 * Mapea una lista de ScoreRemoteDto a una lista de entradas de leaderboard.
 */
fun List<ScoreRemoteDto>.toDomain(): List<LeaderboardEntry> =
    this.map { it.toDomain() }
