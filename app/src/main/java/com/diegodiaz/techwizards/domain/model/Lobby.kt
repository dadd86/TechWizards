package com.diegodiaz.techwizards.domain.model

/**
 * Representa una sala previa a la partida donde los jugadores se agrupan.
 *
 * @property id Identificador global (UUID/ULID).
 * @property nombre Nombre visible del lobby.
 * @property codigo Código corto opcional para invitar jugadores.
 * @property modo Modo de juego (1v1, duos, etc.).
 * @property estado Estado actual del lobby.
 * @property creadorNumero Número del usuario creador (FK a `Usuario.numero`).
 * @property createdAtMs Marca temporal de creación (epoch millis).
 */
data class Lobby(
    val id: String,
    val nombre: String,
    val codigo: String?,         // puede ser null (coincide con Entity)
    val modo: String,
    val estado: LobbyEstado,
    val creadorNumero: Long,
    val createdAtMs: Long,
)

enum class LobbyEstado { PENDING, FULL, CLOSED }
