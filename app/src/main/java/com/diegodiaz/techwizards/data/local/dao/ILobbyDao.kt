package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.LobbyEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe


/**
 * DAO para la tabla `Lobby`.
 *
 * @security
 * - Consultas parametrizadas protegen contra SQL injection.
 * - No registrar PII en logs asociados a estas operaciones.
 */
@Dao
interface ILobbyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(lobby: LobbyEntity)

    @Query("SELECT * FROM Lobby WHERE id = :lobbyId LIMIT 1")
    suspend fun obtenerPorId(lobbyId: String): LobbyEntity?

    @Query("SELECT * FROM Lobby WHERE estado = :estado ORDER BY createdAtMs DESC LIMIT :limite")
    suspend fun listarPorEstado(estado: String, limite: Int): List<LobbyEntity>

    @Query("UPDATE Lobby SET estado = :estado WHERE id = :lobbyId")
    suspend fun actualizarEstado(lobbyId: String, estado: String): Int

    /** Inserta o reemplaza un lobby. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: LobbyEntity): Completable

    /** Lista reactiva de todos los lobbies (para observar cambios). */
    @Query("SELECT * FROM Lobby ORDER BY createdAtMs DESC")
    fun getAll(): Flowable<List<LobbyEntity>>

    /** Obtiene un lobby por id. */
    @Query("SELECT * FROM Lobby WHERE id = :lobbyId LIMIT 1")
    fun getById(lobbyId: String): Maybe<LobbyEntity>

    /** Lista lobbies por estado con límite. */
    @Query("SELECT * FROM Lobby WHERE estado = :estado ORDER BY createdAtMs DESC LIMIT :limite")
    fun listByEstado(estado: String, limite: Int): Flowable<List<LobbyEntity>>

    /** Actualiza el estado de un lobby. */
    @Query("UPDATE Lobby SET estado = :estado WHERE id = :lobbyId")
    fun updateEstado(lobbyId: String, estado: String): Completable


}
