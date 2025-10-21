package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.PartidaEntity
import com.diegodiaz.techwizards.domain.model.Partida

fun PartidaEntity.toDomain() = Partida(id, usuarioId, fecha, resultado, cambioMonedas)
fun Partida.toEntity() = PartidaEntity(id, usuarioId, fecha, resultado, cambioMonedas)