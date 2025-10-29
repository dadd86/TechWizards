package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.UsuarioEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe

/**
 * DAO para operaciones sobre la tabla `Usuario`.
 *
 * @security
 * - Consultas parametrizadas evitan inyección.
 * - Solo se manipulan alias y estados sin almacenar datos sensibles adicionales.
 */
@Dao
interface IUsuarioDao {
    @Query("SELECT * FROM usuario WHERE id = :id")
    fun getById(id: String): Maybe<UsuarioEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(entity: UsuarioEntity): Completable

    @Query("DELETE FROM usuario")
    fun borrarTodo()

    @Query("SELECT * FROM Usuario LIMIT 1")
    suspend fun obtenerUsuarioPrincipal(): UsuarioEntity?

    @Query("UPDATE Usuario SET monedas = :nuevoSaldo WHERE numero = :numero")
    suspend fun actualizarSaldo(numero: Long, nuevoSaldo: Int): Int

    @Query("UPDATE Usuario SET gano = :gano WHERE numero = :numero")
    suspend fun actualizarUltimoResultado(numero: Long, gano: Boolean): Int
}