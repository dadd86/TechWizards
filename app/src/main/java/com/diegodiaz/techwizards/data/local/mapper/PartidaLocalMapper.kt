package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.PartidaEntity
import com.diegodiaz.techwizards.domain.model.Partida

/**
 * Convierte una PartidaEntity (de la base de datos)
 * al modelo de dominio Partida (usado en la lógica del juego).
 */
fun PartidaEntity.toDomain(): Partida =
    Partida(
        id = this.id.hashCode().toLong(), // Convertimos String -> Long
        perder = this.resultado.equals("PERDIDO", ignoreCase = true),
        gano = this.resultado.equals("GANADO", ignoreCase = true),
        deltaMonedas = this.cambioMonedas,
        usuarioId = this.usuarioId
    )

/**
 * Convierte una Partida del dominio a su forma almacenable en Room.
 */
fun Partida.toEntity(): PartidaEntity =
    PartidaEntity(
        id = "match_${this.id}", // Long -> String
        usuarioId = this.usuarioId,
        fecha = System.currentTimeMillis(), // Guarda fecha actual
        resultado = when {
            this.gano -> "GANADO"
            this.perder -> "PERDIDO"
            else -> "INDETERMINADO"
        },
        cambioMonedas = this.deltaMonedas
    )
