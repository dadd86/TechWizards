package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.LobbyEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable


/**
 * DAO para la tabla `Lobby`.
 *
 * @security
 * - Consultas parametrizadas protegen contra SQL injection.
 * - No registrar PII en logs asociados a estas operaciones.
 */
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(lobby: LobbyEntity)

    @Query("SELECT * FROM Lobby WHERE id = :lobbyId LIMIT 1")
    suspend fun obtenerPorId(lobbyId: String): LobbyEntity?

    @Query("SELECT * FROM Lobby WHERE estado = :estado ORDER BY createdAtMs DESC LIMIT :limite")
    suspend fun listarPorEstado(estado: String, limite: Int): List<LobbyEntity>

    @Query("UPDATE Lobby SET estado = :estado WHERE id = :lobbyId")
    suspend fun actualizarEstado(lobbyId: String, estado: String): Int
}
