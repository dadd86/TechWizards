package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.PartidaEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.flow.Flow

@Dao
interface IPartidaDao {

    @Query("SELECT * FROM Partida WHERE usuarioNumero = :usuarioNumero ORDER BY fecha DESC")
    fun historial(usuarioNumero: Long): Flowable<List<PartidaEntity>>

    @Query("SELECT * FROM Partida WHERE usuarioNumero = :usuarioNumero ORDER BY fecha DESC LIMIT :limite")
    fun historialFlow(usuarioNumero: Long, limite: Int): Flow<List<PartidaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertar(entity: PartidaEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarSuspend(entity: PartidaEntity): Long

    @Query("DELETE FROM Partida")
    fun borrarTodo(): Completable
}