package com.diegodiaz.techwizards.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "monedero",
    indices = [Index(value = ["usuarioId"], unique = true)]
)
data class MonederoEntity(
    @PrimaryKey val id: String,
    val usuarioId: String,
    val saldo: Int
)