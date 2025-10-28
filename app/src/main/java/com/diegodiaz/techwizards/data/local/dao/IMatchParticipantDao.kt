package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.MatchParticipantEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

@Dao
interface IMatchParticipantDao {

    // ----- Lecturas -----
    @Query("SELECT * FROM match_participant WHERE id = :id LIMIT 1")
    fun getById(id: Long): Maybe<MatchParticipantEntity>

    @Query("""
        SELECT * FROM match_participant 
        WHERE matchId = :matchId 
        ORDER BY esGanador DESC, joinedAt ASC
    """)
    fun listByMatch(matchId: Long): Flowable<List<MatchParticipantEntity>>

    @Query("""
        SELECT * FROM match_participant 
        WHERE matchId = :matchId AND userId = :userId 
        LIMIT 1
    """)
    fun findByMatchAndUser(matchId: Long, userId: Long): Maybe<MatchParticipantEntity>

    @Query("SELECT COUNT(*) FROM match_participant WHERE matchId = :matchId")
    fun countByMatch(matchId: Long): Single<Int>

    // ----- Escrituras -----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(entity: MatchParticipantEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(list: List<MatchParticipantEntity>): Completable

    @Query("UPDATE match_participant SET esGanador = :winner WHERE id = :id")
    fun updateWinner(id: Long, winner: Boolean): Completable

    @Query("DELETE FROM match_participant WHERE id = :id")
    fun deleteById(id: Long): Completable

    @Query("DELETE FROM match_participant WHERE matchId = :matchId")
    fun deleteByMatch(matchId: Long): Completable
}

