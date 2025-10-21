package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.MonederoEntity
import com.diegodiaz.techwizards.domain.model.Monedero

fun MonederoEntity.toDomain() = Monedero(id, usuarioId, saldo)
fun Monedero.toEntity() = MonederoEntity(id, usuarioId, saldo)