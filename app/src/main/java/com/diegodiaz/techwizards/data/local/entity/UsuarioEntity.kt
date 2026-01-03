package com.diegodiaz.techwizards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room alineada con la tabla `Usuario`.
 *
 * @security
 * - No almacena contraseñas ni correos.
 */
@Entity(tableName = "Usuario")
data class UsuarioEntity(
    @PrimaryKey
    @ColumnInfo(name = "numero")
    val numero: Long,
    @ColumnInfo(name = "usuario")
    val alias: String,
    @ColumnInfo(name = "fechaAlta")
    val fechaAltaMs: Long,
    @ColumnInfo(name = "monedas")
    val monedas: Int,
    @ColumnInfo(name = "gano")
    val ganoUltimaPartida: Boolean,
    @ColumnInfo(name = "firebaseUid")
    val firebaseUid: String?,
)