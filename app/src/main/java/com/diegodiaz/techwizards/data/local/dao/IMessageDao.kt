package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.MessageEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface IMessageDao {

    /**
     * Devuelve todos los mensajes de un lobby, ordenados por fecha ascendente.
     */
    @Query("SELECT * FROM message WHERE lobbyId = :lobbyId ORDER BY fecha ASC")
    fun getMensajes(lobbyId: String): Flowable<List<MessageEntity>>

    /**
     * Inserta o reemplaza un mensaje en la base de datos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(mensaje: MessageEntity): Completable

    /**
     * Borra todos los mensajes asociados a un lobby.
     * (opcional pero útil si limpias datos entre partidas)
     */
    @Query("DELETE FROM message WHERE lobbyId = :lobbyId")
    fun deleteMensajesPorLobby(lobbyId: String): Completable
}
