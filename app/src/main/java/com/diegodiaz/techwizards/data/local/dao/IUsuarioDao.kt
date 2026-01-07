package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.UsuarioEntity
import io.reactivex.rxjava3.core.Maybe
import kotlinx.coroutines.flow.Flow
@Dao
interface IUsuarioDao {

    // Versión coroutines (si la usas en otros casos)
    @Query("SELECT * FROM Usuario LIMIT 1")
    suspend fun obtenerUsuarioPrincipal(): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(usuario: UsuarioEntity): io.reactivex.rxjava3.core.Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSuspend(usuario: UsuarioEntity)


    @Query("SELECT * FROM Usuario WHERE numero = :numero LIMIT 1")
    fun getByNumeroRx(numero: Long): Maybe<UsuarioEntity>

    @Query("SELECT * FROM Usuario WHERE numero = :numero LIMIT 1")
    suspend fun getByNumero(numero: Long): UsuarioEntity?

    /**
     * Obtiene el usuario enlazado al UID de Firebase (si existe).
     *
     * @param firebaseUid UID asociado al usuario autenticado.
     * @return Usuario encontrado o `null` si no hay coincidencia.
     * @security
     * - No expone PII; se usa solo para resolver IDs locales.
     */
    @Query("SELECT * FROM Usuario WHERE firebaseUid = :firebaseUid LIMIT 1")
    suspend fun getByFirebaseUid(firebaseUid: String): UsuarioEntity?

    /**
     * Variante RxJava para resolver usuarios por UID de Firebase.
     *
     * @param firebaseUid UID asociado al usuario autenticado.
     * @return [Maybe] con el usuario si existe.
     * @security
     * - Evita exponer datos sensibles; solo IDs y alias locales.
     */
    @Query("SELECT * FROM Usuario WHERE firebaseUid = :firebaseUid LIMIT 1")
    fun getByFirebaseUidRx(firebaseUid: String): Maybe<UsuarioEntity>


    @Query("UPDATE Usuario SET monedas = :nuevoSaldo WHERE numero = :numero")
    suspend fun actualizarSaldo(numero: Long, nuevoSaldo: Int): Int

    @Query("UPDATE Usuario SET gano = :gano WHERE numero = :numero")
    suspend fun actualizarUltimoResultado(numero: Long, gano: Boolean): Int

    @Query("DELETE FROM usuario")
    fun borrarTodo()

    @Query("SELECT * FROM Usuario WHERE numero = :numero LIMIT 1")
    fun observeUsuario(numero: Long): Flow<UsuarioEntity?>

}