package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.MonederoEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface IMonederoDao {
    @Query("SELECT * FROM Monedero WHERE usuarioNumero = :usuarioNumero LIMIT 1")
    fun observeSaldo(usuarioNumero: Long): Flowable<MonederoEntity>

    @Query("SELECT * FROM Monedero WHERE usuarioNumero = :usuarioNumero LIMIT 1")
    suspend fun getMonederoSimple(usuarioNumero: Long): MonederoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(entity: MonederoEntity): Completable

    @Query("UPDATE Monedero SET saldo = :nuevo WHERE usuarioNumero = :usuarioNumero")
    suspend fun actualizarSaldo(usuarioNumero: Long, nuevo: Int)

    // RxJava
    @Query("UPDATE Monedero SET saldo = :nuevo WHERE usuarioNumero = :usuarioNumero")
    fun actualizarSaldoRx(usuarioNumero: Long, nuevo: Int): Completable

    @Query("DELETE FROM usuario")
    fun borrarTodo()

    /**
     * Inserta o actualiza el saldo del monedero usando corrutinas.
     *
     * @param entity Registro a persistir.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSuspend(entity: MonederoEntity)
}