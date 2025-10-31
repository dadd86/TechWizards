package com.diegodiaz.techwizards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.diegodiaz.techwizards.domain.model.Resultado

/**
 * Representa una partida registrada en la base de datos local.
 * Cada partida pertenece a un usuario y almacena su resultado y las monedas ganadas/perdidas.
 */
@Entity(
    tableName = "Partida",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["numero"],
            childColumns = ["usuarioNumero"],
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
    val usuarioNumero: Long,

    @ColumnInfo(name = "fecha")
    val fecha: Long, // epoch millis

    @ColumnInfo(name = "resultado")
    val resultado: Resultado, // Usa el enum del dominio (VICTORIA / DERROTA)

    @ColumnInfo(name = "cambioMonedas")
    val cambioMonedas: Int
)
