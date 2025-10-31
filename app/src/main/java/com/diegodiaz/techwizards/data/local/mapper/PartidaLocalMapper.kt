package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.PartidaEntity
import com.diegodiaz.techwizards.domain.model.Partida
import com.diegodiaz.techwizards.domain.model.Resultado

/**
 * Mapper entre la entidad de Room (PartidaEntity)
 * y el modelo de dominio (Partida).
 */
fun PartidaEntity.toDomain(): Partida =
    Partida(
        id = id,
        usuarioNumero = usuarioNumero,
        fecha = fecha,
        resultado = resultado,
        deltaMonedas = cambioMonedas
    )

fun Partida.toEntity(): PartidaEntity =
    PartidaEntity(
        id = id,
        usuarioNumero = usuarioNumero,
        fecha = fecha,
        resultado = resultado,
        cambioMonedas = deltaMonedas
    )
