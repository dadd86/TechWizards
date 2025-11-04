package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.PartidaConUsuarioEntity
import com.diegodiaz.techwizards.data.local.entity.PartidaEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface IPartidaDao {

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertar(entity: PartidaEntity): Completable

    @Insert
    suspend fun insert(partida: PartidaEntity): Long

    @Query("DELETE FROM Partida")
    fun borrarTodo(): Completable

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