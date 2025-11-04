package com.diegodiaz.techwizards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

/**
 * Proyección de Room que combina la información de la partida con el alias del jugador.
 *
 * @property partida Datos persistidos en la tabla `Partida`.
 * @property alias Alias visible del jugador almacenado en `Usuario.usuario`.
 */
data class PartidaConUsuarioEntity(
    val id: Long,
    val usuarioNumero: Long,
    val fecha: Long,
    val resultado: Resultado,             // ← requiere TypeConverter String↔Resultado
    @ColumnInfo(name = "cambioMonedas")
    val cambioMonedas: Int,
    @ColumnInfo(name = "alias")
    val alias: String
)
