package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.MatchEventEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
/**
 * DAO para `MatchEvent`.
 *
 * @security
 * - Consultas parametrizadas para impedir SQL injection.
 * - No debe escribirse PII en payloadJson sin previo cifrado.
 */
@Dao
interface IMatchEventDao {

    // ---- Lecturas ----
    @Query("SELECT * FROM match_event WHERE id = :id LIMIT 1")
    fun getById(id: Long): Maybe<MatchEventEntity>

    @Query("SELECT * FROM match_event WHERE matchId = :matchId ORDER BY timestamp ASC")
    fun listByMatch(matchId: Long): Flowable<List<MatchEventEntity>>

    // ---- Escrituras ----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(event: MatchEventEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(events: List<MatchEventEntity>): Completable

    @Query("DELETE FROM match_event WHERE matchId = :matchId")
    fun deleteByMatch(matchId: Long): Completable

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(evento: MatchEventEntity)

    @Query("SELECT * FROM MatchEvent WHERE matchId = :matchId ORDER BY seq ASC")
    suspend fun listarPorMatch(matchId: String): List<MatchEventEntity>

    @Query("SELECT MAX(seq) FROM MatchEvent WHERE matchId = :matchId")
    suspend fun obtenerUltimaSecuencia(matchId: String): Long?
}

