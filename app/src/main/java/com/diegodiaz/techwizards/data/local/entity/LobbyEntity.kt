package com.diegodiaz.techwizards.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.ColumnInfo

@Entity(
    tableName = "Lobby",
    indices = [
        Index(value = ["estado"]),
        Index(value = ["codigo"], unique = true),
        Index(value = ["creadorNum"]),                  // ✅ índice que faltaba
        Index(value = ["estado", "createdAtMs"])        // (opcional) útil para listados por estado/fecha
    ],
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["numero"],                 // PK de UsuarioEntity
            childColumns = ["creadorNum"],              // nombre de columna real
            onDelete = ForeignKey.CASCADE
        ),
    ],
)
data class LobbyEntity(
    @ColumnInfo(name = "nombre")
    val nombre: String,

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "codigo")
    val codigo: String?,

    @ColumnInfo(name = "modo")
    val modo: String,

    @ColumnInfo(name = "estado")
    val estado: String,

    @ColumnInfo(name = "creadorNum")
    val creadorNumero: Long,

    @ColumnInfo(name = "createdAtMs")
    val createdAtMs: Long,
)
