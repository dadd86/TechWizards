package com.diegodiaz.techwizards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "victory_location")
data class VictoryLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val latitude: Double,

    val longitude: Double,

    @ColumnInfo(name = "accuracyMetres")
    val accuracyMetres: Double? = null,

    @ColumnInfo(name = "capturedAtMs")
    val capturedAtMs: Long
)
