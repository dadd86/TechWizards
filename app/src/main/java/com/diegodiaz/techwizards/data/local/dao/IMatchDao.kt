package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.MatchEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

@Dao
interface IMatchDao {

    // ---- Lecturas ----

    /** One-shot: devuelve el match por id si existe. */
    @Query("SELECT * FROM `match` WHERE id = :id LIMIT 1")
    fun getById(id: String): Maybe<MatchEntity>

    /** Stream: lista de partidas de un lobby, ordenadas por inicio (más recientes primero). */
    @Query("SELECT * FROM `match` WHERE lobbyId = :lobbyId ORDER BY inicioEn DESC")
    fun listByLobby(lobbyId: String): Flowable<List<MatchEntity>>

    /** Stream: partidas en curso (sin fecha de fin). */
    @Query("SELECT * FROM `match` WHERE finEn IS NULL ORDER BY inicioEn DESC")
    fun listOngoing(): Flowable<List<MatchEntity>>

    /** One-shot: cuántas partidas activas hay. */
    @Query("SELECT COUNT(*) FROM `match` WHERE inicioEn IS NULL")
    fun countOngoing(): Single<Int>

    // ---- Escrituras ----

    /** Inserta/actualiza un match. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(entity: MatchEntity): Completable

    /** Inserta/actualiza varios matches. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(list: List<MatchEntity>): Completable

    /** Marca un match como finalizado (pone fechaFin). */
    @Query("UPDATE `match` SET finEn = :finishedAt WHERE id = :id")
    fun markFinished(id: String, finishedAt: Long): Completable

    /** Borra un match por id. */
    @Query("DELETE FROM `match` WHERE id = :id")
    fun deleteById(id: String): Completable
}
