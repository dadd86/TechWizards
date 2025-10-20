package com.diegodiaz.techwizards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room que almacena el saldo virtual de un usuario.
 *
 * @property id Identificador del monedero.
 * @property usuarioNum Usuario propietario.
 * @property saldoActual Saldo disponible.
 * @property actualizadoEnMs Marca temporal de la última actualización.
 * @security
 * - Los saldos deben protegerse mediante cifrado en repositorio remoto.
 * - Los accesos a esta tabla deben registrarse para auditoría.
 */
@Entity(
    tableName = "Monedero",
    indices = [Index(value = ["usuarioNum"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["numero"],
            childColumns = ["usuarioNum"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class MonederoEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "usuarioNum")
    val usuarioNum: Long,
    @ColumnInfo(name = "saldoActual")
    val saldoActual: Int,
    @ColumnInfo(name = "actualizadoEnMs")
    val actualizadoEnMs: Long,
)
