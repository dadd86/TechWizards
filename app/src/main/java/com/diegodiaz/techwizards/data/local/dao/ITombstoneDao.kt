package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.TombstoneEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

/**
 * DAO para `Tombstone`.
 *
 * @security
 * - Mantiene registros de borrado sin exponer datos sensibles.
 * - Consultas parametrizadas evitan inyección SQL.
 */
@Dao
interface ITombstoneDao {

    // --- Lecturas ---
    @Query("SELECT * FROM tombstone WHERE id = :id LIMIT 1")
    fun getById(id: Long): Maybe<TombstoneEntity>

    @Query("SELECT * FROM tombstone WHERE type = :type ORDER BY deletedAt DESC")
    fun streamByType(type: String): Flowable<List<TombstoneEntity>>

    @Query("SELECT * FROM tombstone ORDER BY deletedAt DESC")
    fun listAll(): Single<List<TombstoneEntity>>

    // --- Escrituras ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: TombstoneEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<TombstoneEntity>): Completable

    @Query("DELETE FROM tombstone WHERE type = :type AND deletedId = :deletedId")
    fun deleteOne(type: String, deletedId: String): Completable

    @Query("DELETE FROM tombstone")
    fun clearAll(): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: TombstoneEntity)

    @Query("SELECT * FROM Tombstone WHERE tableName = :tableName")
    suspend fun listarPorTabla(tableName: String): List<TombstoneEntity>
}

