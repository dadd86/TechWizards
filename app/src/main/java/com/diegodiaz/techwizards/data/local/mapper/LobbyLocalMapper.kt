package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.LobbyEntity
import com.diegodiaz.techwizards.domain.model.Lobby
import com.diegodiaz.techwizards.domain.model.LobbyEstado

fun LobbyEntity.toDomain(): Lobby =
    Lobby(
        id = id,
        nombre = nombre, // ✅
        codigo = codigo,
        modo = modo,
        estado = LobbyEstado.valueOf(estado),
        creadorNumero = creadorNumero,
        createdAtMs = createdAtMs,
    )

fun Lobby.toEntity(): LobbyEntity =
    LobbyEntity(
        id = id,
        nombre = nombre, // ✅
        codigo = codigo,
        modo = modo,
        estado = estado.name,
        creadorNumero = creadorNumero,
        createdAtMs = createdAtMs,
    )
