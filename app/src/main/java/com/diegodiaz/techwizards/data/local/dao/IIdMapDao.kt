package com.diegodiaz.techwizards.data.local.dao


import com.diegodiaz.techwizards.data.local.entity.IdMapEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * DAO para la tabla `IdMap`.
 *
 * @security
 * - Utiliza consultas parametrizadas para impedir inyección SQL.
 * - No expone valores sensibles; solo claves técnicas.
 */
@Dao
interface IIdMapDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: IdMapEntity)

    @Query("SELECT * FROM IdMap WHERE localTable = :localTable AND localId = :localId LIMIT 1")
    suspend fun obtener(localTable: String, localId: String): IdMapEntity?
}
