package com.diegodiaz.techwizards.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tombstone",
    indices = [
        Index(value = ["type", "deletedId"], unique = true), // evita duplicados por recurso
        Index("deletedAt")
    ]
)
data class TombstoneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val type: String,
    val deletedId: String,
    val deletedAt: Long
)
