package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.UsuarioEntity
import com.diegodiaz.techwizards.domain.model.Usuario

/**
 * Convierte una entidad de base de datos (UsuarioEntity)
 * en un modelo de dominio (Usuario) usado por la lógica del juego.
 */
fun UsuarioEntity.toDomain(): Usuario =
    Usuario(
        id = this.id,
        nombre = this.nombre,
        monedas = this.monedas,
        alias = null,             // No se guarda en la base de datos
        avatarUrl = null,         // Tampoco se guarda
        creadoEn = System.currentTimeMillis() // Valor generado al cargar
    )

/**
 * Convierte un modelo de dominio (Usuario)
 * en una entidad almacenable en Room (UsuarioEntity).
 */
fun Usuario.toEntity(): UsuarioEntity =
    UsuarioEntity(
        id = this.id,
        nombre = this.nombre,
        monedas = this.monedas
    )
