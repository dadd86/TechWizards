package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.LobbyEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface ILobbyDao {

    // Devuelve todos los lobbies guardados (flujo observable con RxJava)
    @Query("SELECT * FROM lobby")
    fun getAll(): Flowable<List<LobbyEntity>>

    // Inserta un nuevo lobby o lo reemplaza si ya existe
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(lobby: LobbyEntity): Completable

    // Cierra un lobby cambiando su flag "abierta" a 0 (falso)
    @Query("UPDATE lobby SET abierta = 0 WHERE id = :lobbyId")
    fun cerrarLobby(lobbyId: String): Completable
}
