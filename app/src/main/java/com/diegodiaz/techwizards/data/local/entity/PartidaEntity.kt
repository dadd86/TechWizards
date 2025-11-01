package com.diegodiaz.techwizards.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo


@Entity(
    tableName = "Partida",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["numero"],        // ✅ ahora apunta correctamente al PK de Usuario
            childColumns = ["usuarioNumero"],  // ✅ nombre alineado
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["usuarioNumero"]),
        Index(value = ["usuarioNumero", "fecha"])
    ]
)
data class PartidaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "usuarioNumero")
    val usuarioNumero: Long,                  // ✅ tipo compatible con UsuarioEntity.numero

    @ColumnInfo(name = "fecha")
    val fecha: Long,

    @ColumnInfo(name = "resultado")
    val resultado: Resultado,

    @ColumnInfo(name = "cambioMonedas")
    val cambioMonedas: Int
)

enum class Resultado { GANADO, PERDIDO }