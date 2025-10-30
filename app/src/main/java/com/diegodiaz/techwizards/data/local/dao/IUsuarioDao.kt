package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.UsuarioEntity
import io.reactivex.rxjava3.core.Maybe

@Dao
interface IUsuarioDao {

    // Versión coroutines (si la usas en otros casos)
    @Query("SELECT * FROM Usuario LIMIT 1")
    suspend fun obtenerUsuarioPrincipal(): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(usuario: UsuarioEntity): io.reactivex.rxjava3.core.Completable


    @Query("SELECT * FROM Usuario WHERE numero = :numero LIMIT 1")
    fun getByNumeroRx(numero: Long): Maybe<UsuarioEntity>

    @Query("UPDATE Usuario SET monedas = :nuevoSaldo WHERE numero = :numero")
    suspend fun actualizarSaldo(numero: Long, nuevoSaldo: Int): Int

    @Query("UPDATE Usuario SET gano = :gano WHERE numero = :numero")
    suspend fun actualizarUltimoResultado(numero: Long, gano: Boolean): Int

    @Query("DELETE FROM usuario")
    fun borrarTodo()

}
