package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.MatchScoreEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe

@Dao
interface IMatchScoreDao {

    // ----- Lecturas -----
    @Query("SELECT * FROM match_score WHERE id = :id LIMIT 1")
    fun getById(id: Long): Maybe<MatchScoreEntity>

    @Query("SELECT * FROM match_score WHERE matchId = :matchId ORDER BY puntos DESC")
    fun listByMatch(matchId: Long): Flowable<List<MatchScoreEntity>>

    // ----- Escrituras -----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(entity: MatchScoreEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(list: List<MatchScoreEntity>): Completable

    @Query("UPDATE match_score SET puntos = :points WHERE id = :id")
    fun updateScore(id: Long, points: Int): Completable

    @Query("DELETE FROM match_score WHERE id = :id")
    fun deleteById(id: Long): Completable

    @Query("DELETE FROM match_score WHERE matchId = :matchId")
    fun deleteByMatch(matchId: Long): Completable
}

