package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.MonederoEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

/**
 * DAO de acceso al monedero alineado con la tabla `Monedero` del script SQL base.
 *
 * @security
 * - Solo gestiona saldos y referencias locales, sin datos sensibles adicionales.
 */
@Dao
interface IMonederoDao {

    /**
     * Observa el saldo del monedero asociado a un usuario concreto.
     *
     * @param usuarioNumero Identificador local del jugador.
     * @return [Flowable] que emite el saldo actualizado.
     * @throws IllegalStateException No lanza excepciones; Room gestiona los errores.
     * @security
     * - Exponen únicamente saldos numéricos vinculados a IDs locales.
     */
    @Query("SELECT * FROM Monedero WHERE usuarioNumero = :usuarioNumero LIMIT 1")
    fun observeSaldo(usuarioNumero: Long): Flowable<MonederoEntity>

    /**
     * Obtiene el monedero sincrónicamente para inicializaciones.
     *
     * @param usuarioNumero Identificador local del jugador.
     * @return Monedero encontrado o `null` si no existe.
     * @throws IllegalStateException No lanza excepciones controladas.
     * @security
     * - Uso interno para sincronizar saldo inicial.
     */
    @Query("SELECT * FROM Monedero WHERE usuarioNumero = :usuarioNumero LIMIT 1")
    suspend fun getMonederoSimple(usuarioNumero: Long): MonederoEntity?

    /**
     * Inserta o actualiza el monedero usando la API RxJava de Room.
     *
     * @param entity Registro a persistir.
     * @return Operación completada como [Completable].
     * @throws IllegalStateException Room propaga errores SQL.
     * @security
     * - No se escriben datos sensibles en logs; delega en Room.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(entity: MonederoEntity): Completable

    /**
     * Inserta o actualiza el saldo del monedero usando corrutinas.
     *
     * @param entity Registro a persistir.
     * @return Unit tras completar la operación.
     * @throws IllegalStateException Room propaga cualquier violación de constraint.
     * @security
     * - Solo opera con IDs locales y saldos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSuspend(entity: MonederoEntity)

    /**
     * Actualiza el saldo almacenado para un usuario específico.
     *
     * @param usuarioNumero Identificador local del jugador.
     * @param nuevo Nuevo saldo a persistir.
     * @return Unit; Room informa mediante excepciones en caso de error.
     * @throws IllegalStateException Room gestiona constraints de integridad.
     * @security
     * - No almacena historiales; solo muta el saldo.
     */
    @Query("UPDATE Monedero SET saldo = :nuevo WHERE usuarioNumero = :usuarioNumero")
    suspend fun actualizarSaldo(usuarioNumero: Long, nuevo: Int)

    /**
     * Variante reactiva para actualizaciones de saldo usando RxJava.
     *
     * @param usuarioNumero Identificador del jugador.
     * @param nuevo Nuevo saldo calculado.
     * @return [Completable] que finaliza cuando Room guarda la actualización.
     * @throws IllegalStateException Room lanza excepciones ante errores de constraint.
     * @security
     * - Mantiene el mismo alcance de datos que la versión suspendida.
     */
    @Query("UPDATE Monedero SET saldo = :nuevo WHERE usuarioNumero = :usuarioNumero")
    fun actualizarSaldoRx(usuarioNumero: Long, nuevo: Int): Completable

    /**
     * Limpia la información persistida durante pruebas o depuración.
     *
     * @return Unit tras ejecutar el borrado.
     * @throws IllegalStateException Room propaga errores de integridad referencial.
     * @security
     * - Úsese únicamente en entornos controlados; elimina saldos locales.
     */
    @Query("DELETE FROM usuario")
    fun borrarTodo()
}