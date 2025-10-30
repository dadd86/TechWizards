// Entity
package com.diegodiaz.techwizards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Monedero")
data class MonederoEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "usuarioNumero") val usuarioNumero: Long,
    @ColumnInfo(name = "saldo") val saldo: Int
)
