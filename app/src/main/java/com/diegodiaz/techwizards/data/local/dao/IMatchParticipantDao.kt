package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.MatchParticipantEntity


/**
 * DAO para `MatchParticipant`.
 *
 * @security
 * - Consultas parametrizadas protegen contra inyección.
 * - Solo manipula identificadores técnicos de jugadores.
 */
@Dao
interface IMatchParticipantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(participante: MatchParticipantEntity)

    @Query("SELECT * FROM MatchParticipant WHERE matchId = :matchId")
    suspend fun listarPorMatch(matchId: String): List<MatchParticipantEntity>
}
