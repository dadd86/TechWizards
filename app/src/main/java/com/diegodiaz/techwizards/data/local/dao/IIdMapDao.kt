package com.diegodiaz.techwizards.data.local.dao

import androidx.room.*
import com.diegodiaz.techwizards.data.local.entity.IdMapEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

@Dao
interface IIdMapDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(entity: IdMapEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(list: List<IdMapEntity>): Completable

    @Query("SELECT * FROM id_map WHERE type = :type AND localId = :localId LIMIT 1")
    fun findByLocal(type: String, localId: String): Maybe<IdMapEntity>

    @Query("SELECT * FROM id_map WHERE type = :type AND remoteId = :remoteId LIMIT 1")
    fun findByRemote(type: String, remoteId: String): Maybe<IdMapEntity>

    @Query("DELETE FROM id_map WHERE type = :type AND localId = :localId")
    fun deleteByLocal(type: String, localId: String): Completable

    @Query("SELECT * FROM id_map WHERE type = :type")
    fun listByType(type: String): Single<List<IdMapEntity>>
}
