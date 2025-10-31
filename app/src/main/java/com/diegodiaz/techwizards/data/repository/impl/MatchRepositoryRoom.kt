package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.data.local.dao.IMatchDao
import com.diegodiaz.techwizards.data.local.dao.IMatchEventDao
import com.diegodiaz.techwizards.data.local.dao.IMatchScoreDao
import com.diegodiaz.techwizards.data.local.mapper.toDomain
import com.diegodiaz.techwizards.data.local.mapper.toEntity
import com.diegodiaz.techwizards.domain.model.Match
import com.diegodiaz.techwizards.domain.model.MatchEvent
import com.diegodiaz.techwizards.domain.model.MatchEstado
import com.diegodiaz.techwizards.domain.model.MatchScore
import com.diegodiaz.techwizards.domain.repository.MatchRepository
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger

/**
 * Implementación Room de [MatchRepository].
 */
class MatchRepositoryRoom(

private val matchDao: IMatchDao,
private val matchEventDao: IMatchEventDao,
private val matchScoreDao: IMatchScoreDao,
) : MatchRepository {

    override suspend fun upsertMatch(match: Match): Result<Unit, AgentError> =
        wrap {
            matchDao.upsert(match.toEntity())
            DecentralizedLogger.i("MatchRepository", "Match persistido id=${redact(match.id)} estado=${match.estado}")
            Unit
        }

    override suspend fun registrarEvento(evento: MatchEvent): Result<Unit, AgentError> =
        wrap {
            val lastSeq = matchEventDao.obtenerUltimaSecuencia(evento.matchId) ?: -1
            require(evento.seq > lastSeq) { "Secuencia repetida" }
            matchEventDao.insertar(evento.toEntity())
            DecentralizedLogger.i(
                "MatchRepository",
                "Evento registrado match=${redact(evento.matchId)} seq=${evento.seq}"
            )
            Unit
        }

    override suspend fun obtenerHistorial(limite: Int): Result<List<Match>, AgentError> =
        wrap {
            matchDao.listarPorEstado(MatchEstado.FINISHED.name, limite).map { it.toDomain() }
        }

    override suspend fun guardarScore(score: MatchScore): Result<Unit, AgentError> =
        wrap {
            matchScoreDao.upsert(score.toEntity())
            DecentralizedLogger.i(
                "MatchRepository",
                "Score registrado match=${redact(score.matchId)} usuario=${score.usuarioNumero}"
            )
            Unit
        }

    private suspend fun <T> wrap(block: suspend () -> T): Result<T, AgentError> =
        try {
            Result.Ok(block())
        } catch (ex: IllegalArgumentException) {
            Result.Err(AgentError.Validation(ex.message ?: "Validación"))
        } catch (ex: Throwable) {
            Result.Err(AgentError.Database(ex))
        }

    private fun redact(value: String): String =
        if (value.length <= 4) "***" else value.take(2) + "***" + value.takeLast(2)
}