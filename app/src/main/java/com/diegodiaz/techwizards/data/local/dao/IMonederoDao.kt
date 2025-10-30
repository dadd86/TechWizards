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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(entity: MonederoEntity): Completable

    @Query("UPDATE monedero SET saldo = :nuevo WHERE usuarioNumero = :usuarioNumero")
    fun actualizarSaldo(usuarioNumero: Long, nuevo: Int): Completable

    @Query("DELETE FROM usuario")
    fun borrarTodo()
}