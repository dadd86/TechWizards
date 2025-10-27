package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.MatchEventEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface IMatchEventDao {

    /**
     * Devuelve todos los eventos registrados en una partida concreta.
     */
    @Query("SELECT * FROM match_event WHERE matchId = :matchId ORDER BY timestamp ASC")
    fun getEventosPorPartida(matchId: String): Flowable<List<MatchEventEntity>>

    /**
     * Inserta un nuevo evento o lo reemplaza si ya existe.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarEvento(evento: MatchEventEntity): Completable

    /**
     * Elimina todos los eventos asociados a una partida.
     */
    @Query("DELETE FROM match_event WHERE matchId = :matchId")
    fun borrarEventosPorPartida(matchId: String): Completable
}
