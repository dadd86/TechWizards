package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.UsuarioEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe

@Dao
interface IUsuarioDao {
    @Query("SELECT * FROM usuario WHERE id = :id")
    fun getById(id: String): Maybe<UsuarioEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(entity: UsuarioEntity): Completable
}