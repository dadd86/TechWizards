package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.MonederoEntity
import com.diegodiaz.techwizards.domain.model.Monedero

/**
 * Convierte entre el Monedero de la base de datos (Entity)
 * y el Monedero del dominio (Model).
 */
fun MonederoEntity.toDomain(): Monedero =
    Monedero(
        id = this.id.hashCode(),  // Convertimos el String id a Int único
        monedas = this.saldo      // saldo → monedas
    )

/**
 * Convierte un Monedero del dominio a su versión de base de datos.
 * Se requiere el usuarioId porque el modelo de dominio no lo incluye.
 */
fun Monedero.toEntity(usuarioId: String): MonederoEntity =
    MonederoEntity(
        id = "wallet_${this.id}",  // Convertimos Int a String único
        usuarioId = usuarioId,     // Se inyecta desde el repositorio
        saldo = this.monedas       // monedas → saldo
    )
