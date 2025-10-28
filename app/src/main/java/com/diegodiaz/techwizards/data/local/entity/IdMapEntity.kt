package com.diegodiaz.techwizards.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Mapea IDs locales - remotos para sincronización.
 * type: nombre lógico del recurso ("usuario","match","message"..)
 */
@Entity(tableName = "id_map", /* índices si quieres */)
data class IdMapEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val type: String,
    val localId: String,
    val remoteId: String?,   // mismo tipo que en dominio
    val updatedAt: Long
)
