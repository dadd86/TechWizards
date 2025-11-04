package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.PartidaConUsuarioEntity
import com.diegodiaz.techwizards.data.local.entity.PartidaEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

/**
 * DAO que administra las partidas registradas y su asociación con el alias del jugador.
 *
 * @security
 * - Únicamente gestiona información de juego y alias almacenados localmente.
 */
@Dao
interface IPartidaDao {

    /**
     * Obtiene el historial completo utilizando RxJava, incluyendo el alias más reciente.
     *
     * @param usuarioNumero Identificador local del jugador.
     * @return Flujo reactivo con la lista de partidas ordenada por fecha descendente.
     * @throws IllegalStateException Room propaga las violaciones de integridad.
     * @security
     * - No expone identificadores remotos ni tokens.
     */
    @Query(
        """
        SELECT
            p.id            AS id,
            p.usuarioNumero AS usuarioNumero,
            p.fecha         AS fecha,
            p.resultado     AS resultado,
            p.cambioMonedas AS cambioMonedas,
            u.usuario       AS alias
        FROM Partida p
        INNER JOIN Usuario u ON u.numero = p.usuarioNumero
        WHERE p.usuarioNumero = :usuarioNumero
        ORDER BY p.fecha DESC
        """
    )
    fun historial(usuarioNumero: Long): Flowable<List<PartidaConUsuarioEntity>>

    /**
     * Inserta o reemplaza una partida usando la API RxJava.
     *
     * @param entity Partida a persistir.
     * @return [Completable] que finaliza cuando la operación se guarda.
     * @throws IllegalStateException Room lanza excepciones por violaciones de FK.
     * @security
     * - No incluye payloads sensibles, solo resultados del juego.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertar(entity: PartidaEntity): Completable

    /**
     * Inserta una partida con API de corrutinas, devolviendo el id generado.
     *
     * @param partida Partida a almacenar.
     * @return Identificador autogenerado por SQLite.
     * @throws IllegalStateException Room propaga violaciones de integridad.
     * @security
     * - Registra únicamente información del juego.
     */
    @Insert
    suspend fun insert(partida: PartidaEntity): Long

    /**
     * Elimina todos los registros de partida (uso para pruebas).
     *
     * @return [Completable] que finaliza al borrar la tabla.
     * @throws IllegalStateException Room notifica errores de FK en cascada.
     * @security
     * - Úsese solo en entornos controlados; borra historial local.
     */
    @Query("DELETE FROM Partida")
    fun borrarTodo(): Completable

    /**
     * Observa el historial reciente de partidas limitando la cantidad.
     *
     * @param usuarioNumero Identificador local del jugador.
     * @param limit Cantidad máxima de elementos a recuperar.
     * @return Flujo de corrutinas con las partidas ordenadas por fecha.
     * @throws IllegalStateException Room lanza errores ante problemas de consulta.
     * @security
     * - Solo expone alias y datos de juego.
     */
    @Query(
        """
        SELECT
            p.id            AS id,
            p.usuarioNumero AS usuarioNumero,
            p.fecha         AS fecha,
            p.resultado     AS resultado,
            p.cambioMonedas AS cambioMonedas,
            u.usuario       AS alias
        FROM Partida p
        INNER JOIN Usuario u ON u.numero = p.usuarioNumero
        WHERE p.usuarioNumero = :usuarioNumero
        ORDER BY p.fecha DESC
        LIMIT :limit
        """
    )
    fun observarHistorial(
        usuarioNumero: Long,
        limit: Int = 50
    ): kotlinx.coroutines.flow.Flow<List<PartidaConUsuarioEntity>>
}