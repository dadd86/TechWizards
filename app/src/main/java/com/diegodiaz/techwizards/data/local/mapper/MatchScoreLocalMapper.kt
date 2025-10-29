package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.MatchScoreEntity
import com.diegodiaz.techwizards.domain.model.MatchScore
/**
 * Conversión entre puntajes persistidos y dominio.
 *
 * @security
 * - Solo traslada valores numéricos y claves internas.
 */
fun MatchScoreEntity.toDomain(): MatchScore =
    MatchScore(
        matchId = matchId,
        usuarioNumero = usuarioNumero,
        score = score,
    )

fun MatchScore.toEntity(): MatchScoreEntity =
    MatchScoreEntity(
        matchId = matchId,
        usuarioNumero = usuarioNumero,
        score = score,
    )