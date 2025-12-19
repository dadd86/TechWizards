package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.MatchEntity


/**
 * DAO para la tabla `Match`.
 *
 * @security
 * - Todas las consultas están parametrizadas evitando SQL injection.
 * - Se recomienda enmascarar IDs al loguear desde capas superiores.
 */
@Dao
interface IMatchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(match: MatchEntity)

    @Query("SELECT * FROM Match WHERE estado = :estado ORDER BY finishedAtMs DESC, createdAtMs DESC LIMIT :limite")
    suspend fun listarPorEstado(estado: String, limite: Int): List<MatchEntity>

    @Query("SELECT * FROM Match WHERE id = :matchId LIMIT 1")
    suspend fun obtenerPorId(matchId: String): MatchEntity?

    @Query("SELECT * FROM Match WHERE id = :matchId LIMIT 1")
    fun observarPorId(matchId: String): kotlinx.coroutines.flow.Flow<MatchEntity?>
}
