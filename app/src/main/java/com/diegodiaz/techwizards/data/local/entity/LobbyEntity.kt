package com.diegodiaz.techwizards.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lobby")
data class LobbyEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val capacidad: Int,
    val abierta: Boolean
)
