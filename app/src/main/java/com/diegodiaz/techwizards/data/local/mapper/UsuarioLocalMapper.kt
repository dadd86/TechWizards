package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.UsuarioEntity
import com.diegodiaz.techwizards.domain.model.Usuario

fun UsuarioEntity.toDomain() = Usuario(id, nombre, monedas)
fun Usuario.toEntity() = UsuarioEntity(id, nombre, monedas)