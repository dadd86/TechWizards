package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.TombstoneEntity

/**
 * DAO para `Tombstone`.
 *
 * @security
 * - Mantiene registros de borrado sin exponer datos sensibles.
 * - Consultas parametrizadas evitan inyección SQL.
 */
@Dao
interface ITombstoneDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: TombstoneEntity)

    @Query("SELECT * FROM Tombstone WHERE tableName = :tableName")
    suspend fun listarPorTabla(tableName: String): List<TombstoneEntity>
}
