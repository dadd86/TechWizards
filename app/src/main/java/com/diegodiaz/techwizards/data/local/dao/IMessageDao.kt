package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.MessageEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe


@Dao
interface IMessageDao {

    @Query("SELECT * FROM message WHERE id = :id LIMIT 1")
    fun getById(id: Long): Maybe<MessageEntity>

    // Mensajes por match, ordenados por tiempo ascendente
    @Query("SELECT * FROM message WHERE matchId = :matchId ORDER BY timestamp ASC")
    fun listByMatch(matchId: Long): Flowable<List<MessageEntity>>

    // Mensajes de un usuario dentro de un match
    @Query("SELECT * FROM message WHERE matchId = :matchId AND remitenteId = :userId ORDER BY timestamp ASC")
    fun listByMatchAndUser(matchId: Long, userId: Long): Flowable<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(entity: MessageEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(list: List<MessageEntity>): Completable

    @Query("DELETE FROM message WHERE matchId = :matchId")
    fun deleteByMatch(matchId: Long): Completable

    @Query("SELECT * FROM message WHERE matchId = :lobbyId ORDER BY timestamp ASC")
    fun getMensajes(lobbyId: String): Flowable<List<MessageEntity>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: MessageEntity): Completable
}
