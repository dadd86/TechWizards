package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.PartidaConUsuarioEntity
import com.diegodiaz.techwizards.data.local.entity.PartidaEntity
import com.diegodiaz.techwizards.domain.model.Partida

/**
 * Convierte la proyección de Room en un modelo de dominio listo para UI.
 *
 * @return Modelo de dominio [Partida] enriquecido con el alias persistido al jugar.
 * @throws IllegalStateException No lanza excepciones; la proyección garantiza datos completos.
 * @security
 * - Propaga únicamente alias y resultados de partidas.
 */
fun PartidaConUsuarioEntity.toDomain(): Partida =
    partida.toDomain(partida.nombreJugador.ifBlank { alias })

/**
 * Convierte la entidad simple en dominio adjuntando el alias proporcionado.
 *
 * @param alias Alias vigente del jugador al registrar la partida.
 * @return Instancia de [Partida] lista para la capa de dominio.
 * @throws IllegalArgumentException Se espera un alias no vacío (validado a nivel superior).
 * @security
 * - No muta información sensible; solo copia campos de juego.
 */
fun PartidaEntity.toDomain(alias: String): Partida = Partida(
    id = id,
    usuarioNumero = usuarioNumero,
    aliasJugador = alias,
    fecha = fecha,
    resultado = resultado,
    deltaMonedas = cambioMonedas
)

/**
 * Convierte una partida de dominio en entidad Room.
 *
 * @return Entidad [PartidaEntity] compatible con el esquema definido en `PrimerSQL.sql`.
 * @throws IllegalStateException No lanza excepciones; los tipos ya están validados.
 * @security
 * - No introduce datos adicionales; mantiene integridad con Room.
 */
fun Partida.toEntity() = PartidaEntity(
    id = id,
    usuarioNumero = usuarioNumero,
    fecha = fecha,
    resultado = resultado,
    cambioMonedas = deltaMonedas,
    nombreJugador = aliasJugador
)