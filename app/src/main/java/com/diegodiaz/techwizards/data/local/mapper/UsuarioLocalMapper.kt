package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.UsuarioEntity
import com.diegodiaz.techwizards.domain.model.Usuario

/**
 * Mapeo entre entidad Room y dominio de Usuario.
 *
 * @security Mantiene campos alineados con el esquema SQL.
 */
fun UsuarioEntity.toDomain(): Usuario =
    Usuario(
        numero = numero,
        alias = alias,
        fechaAltaMs = fechaAltaMs,
        monedas = monedas,
        ganoUltimaPartida = ganoUltimaPartida,
        firebaseUid = firebaseUid,
    )

fun Usuario.toEntity(): UsuarioEntity =
    UsuarioEntity(
        numero = numero,
        alias = alias,
        fechaAltaMs = fechaAltaMs,
        monedas = monedas,
        ganoUltimaPartida = ganoUltimaPartida,
        firebaseUid = firebaseUid,
    )
