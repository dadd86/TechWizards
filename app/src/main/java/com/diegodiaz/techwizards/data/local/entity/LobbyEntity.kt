package com.diegodiaz.techwizards.data.local.entity

@Entity(tableName = "lobby")
data class LobbyEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val capacidad: Int,
    val abierta: Boolean
)
