package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.MatchScoreEntity

/**
 * DAO para `MatchScore`.
 *
 * @security
 * - Opera solo con enteros y claves técnicas evitando PII.
 * - Consultas parametrizadas mitigan inyección SQL.
 */
@Dao
interface IMatchScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(score: MatchScoreEntity)

    @Query("SELECT * FROM MatchScore WHERE matchId = :matchId")
    suspend fun listarPorMatch(matchId: String): List<MatchScoreEntity>
}
