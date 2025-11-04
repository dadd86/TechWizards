package com.diegodiaz.techwizards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

/**
 * Proyección de Room que combina la información de la partida con el alias del jugador.
 *
 * @property partida Datos persistidos en la tabla `Partida`.
 * @property alias Alias visible del jugador almacenado en `Usuario.usuario`.
 * @security
 * - Solo expone alias y resultados; no incluye identificadores remotos.
 */
data class PartidaConUsuarioEntity(
    @Embedded val partida: PartidaEntity,
    @ColumnInfo(name = "alias") val alias: String
)