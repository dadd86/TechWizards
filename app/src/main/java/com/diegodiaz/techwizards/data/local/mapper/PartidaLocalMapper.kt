package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.PartidaEntity
import com.diegodiaz.techwizards.data.local.entity.PartidaConUsuarioEntity
import com.diegodiaz.techwizards.domain.model.Partida

/**
 * Convierte la proyección de Room en un modelo de dominio listo para UI.
 */
fun PartidaConUsuarioEntity.toDomain(): Partida = partida.toDomain(alias)

/**
 * Convierte la entidad simple en dominio adjuntando el alias proporcionado.
 */
fun PartidaEntity.toDomain(aliasJugador: String): Partida = Partida(
    id = id,
    usuarioNumero = usuarioNumero,
    aliasJugador = aliasJugador,
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