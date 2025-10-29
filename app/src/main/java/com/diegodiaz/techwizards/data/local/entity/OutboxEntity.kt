package com.diegodiaz.techwizards.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "outbox",
    indices = [Index("tipo"), Index("entregado"), Index("creadoEn")]
)
data class OutboxEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val tipo: String,
    val payload: String,
    val creadoEn: Long,
    val entregado: Boolean,
    val reintentos: Int
)

