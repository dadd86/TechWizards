package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.MonederoEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.flow.Flow

@Dao
interface IMonederoDao {
    @Query("SELECT * FROM Monedero WHERE usuarioNumero = :usuarioNumero LIMIT 1")
    fun observeSaldo(usuarioNumero: Long): Flowable<MonederoEntity>

    @Query("SELECT * FROM Monedero WHERE usuarioNumero = :usuarioNumero LIMIT 1")
    fun observeSaldoFlow(usuarioNumero: Long): Flow<MonederoEntity?>

    @Query("SELECT * FROM Monedero WHERE usuarioNumero = :usuarioNumero LIMIT 1")
    suspend fun obtenerSaldo(usuarioNumero: Long): MonederoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(entity: MonederoEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSuspend(entity: MonederoEntity)

    @Query("UPDATE monedero SET saldo = :nuevo WHERE usuarioNumero = :usuarioNumero")
    fun actualizarSaldo(usuarioNumero: Long, nuevo: Int): Completable

    @Query("UPDATE monedero SET saldo = :nuevo WHERE usuarioNumero = :usuarioNumero")
    suspend fun actualizarSaldoSuspend(usuarioNumero: Long, nuevo: Int): Int

    @Query("DELETE FROM usuario")
    fun borrarTodo()
}