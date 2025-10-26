package com.diegodiaz.techwizards.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "lobby",
    indices = [Index(value = ["nombre"], unique = true)]
)
data class LobbyEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val capacidad: Int,
    val abierta: Boolean
)
