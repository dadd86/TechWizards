package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.MatchEventEntity
/**
 * DAO para `MatchEvent`.
 *
 * @security
 * - Consultas parametrizadas para impedir SQL injection.
 * - No debe escribirse PII en payloadJson sin previo cifrado.
 */
@Dao
interface IMatchEventDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(evento: MatchEventEntity)

    @Query("SELECT * FROM MatchEvent WHERE matchId = :matchId ORDER BY seq ASC")
    suspend fun listarPorMatch(matchId: String): List<MatchEventEntity>

    @Query("SELECT MAX(seq) FROM MatchEvent WHERE matchId = :matchId")
    suspend fun obtenerUltimaSecuencia(matchId: String): Long?
}
