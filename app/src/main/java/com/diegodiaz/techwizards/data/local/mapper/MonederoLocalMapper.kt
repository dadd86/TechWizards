// Mapper
package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.MonederoEntity
import com.diegodiaz.techwizards.domain.model.Monedero

fun MonederoEntity.toDomain(): Monedero =
    Monedero(
        id = id,
        usuarioNumero = usuarioNumero,
        saldo = saldo
    )

fun Monedero.toEntity(): MonederoEntity =
    MonederoEntity(
        id = id,
        usuarioNumero = usuarioNumero,
        saldo = saldo
    )
