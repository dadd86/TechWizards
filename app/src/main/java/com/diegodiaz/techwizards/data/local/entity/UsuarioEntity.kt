package com.diegodiaz.techwizards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room que persiste los usuarios locales.
 *
 * @property numero Identificador incremental.
 * @property usuario Alias único.
 * @property fechaAlta Fecha de alta en milisegundos.
 * @property monedas Saldo principal de monedas.
 * @property gano Indicador de victoria o recompensa.
 * @property firebaseUid UID remoto opcional vinculado a Firebase Auth.
 * @security
 * - `usuario` debe pasar por filtros de moderación y longitud.
 * - `firebaseUid` nunca debe exponerse en texto plano sin cifrado.
 */
@Entity(
    tableName = "Usuario",
    indices = [Index(value = ["usuario"], unique = true), Index(value = ["firebaseUid"], unique = true)],
)
data class UsuarioEntity(
    @PrimaryKey
    @ColumnInfo(name = "numero")
    val numero: Long,
    @ColumnInfo(name = "usuario")
    val usuario: String,
    @ColumnInfo(name = "fechaAlta")
    val fechaAlta: Long,
    @ColumnInfo(name = "monedas")
    val monedas: Int,
    @ColumnInfo(name = "gano")
    val gano: Boolean,
    @ColumnInfo(name = "firebaseUid")
    val firebaseUid: String?,
)
