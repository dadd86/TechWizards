package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.LobbyEntity
import com.diegodiaz.techwizards.domain.model.Lobby

/**
 * Convierte entre LobbyEntity (base de datos local)
 * y Lobby (modelo de dominio), mapeando los campos compatibles.
 */
fun LobbyEntity.toDomain(): Lobby =
    Lobby(
        id = this.id.hashCode().toLong(), // convertimos String → Long de forma segura
        nombre = this.nombre,
        ownerId = 0L,                    // no existe en la BD, valor por defecto
        participanteIds = emptyList(),   // idem
        creadoEn = System.currentTimeMillis(),
        esPublico = this.abierta         // 'abierta' ≈ 'esPublico'
    )

fun Lobby.toEntity(): LobbyEntity =
    LobbyEntity(
        id = this.id.toString(),         // convertimos Long → String para Room
        nombre = this.nombre,
        capacidad = this.participanteIds.size, // usamos nº de participantes como capacidad
        abierta = this.esPublico
    )
