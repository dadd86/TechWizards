package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.PartidaEntity
import com.diegodiaz.techwizards.domain.model.Partida


fun PartidaEntity.toDomain() = Partida(
    id = id,
    usuarioNumero = usuarioNumero,
    fecha = fecha,
    resultado = resultado,
    deltaMonedas = cambioMonedas
)

fun Partida.toEntity() = PartidaEntity(
    id = id,
    usuarioNumero = usuarioNumero,
    fecha = fecha,
    resultado = resultado,
    cambioMonedas = deltaMonedas
)