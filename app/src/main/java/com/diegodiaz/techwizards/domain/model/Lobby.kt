package com.diegodiaz.techwizards.domain.model


/**
 * Representa una sala previa a la partida donde los jugadores se agrupan.
 *
 * @property id Identificador global (UUID/ULID).
 * @property codigo Código corto opcional compartible para invitar jugadores.
 * @property modo Modo de juego (1v1, duos, etc.).
 * @property estado Estado actual del lobby.
 * @property creadorNumero Número del usuario creador (FK a `Usuario.numero`).
 * @property createdAtMs Marca temporal de creación (epoch millis).
 * @security
 * - No incluye datos sensibles de jugadores.
 */
data class Lobby(
    val id: String,
    val codigo: String?,
    val modo: String,
    val estado: LobbyEstado,
    val creadorNumero: Long,
    val createdAtMs: Long,
)

/**
 * Estados válidos del lobby según el esquema SQL.
 *
 * @security Solo se permiten valores definidos para evitar inyecciones.
 */
enum class LobbyEstado { PENDING, FULL, CLOSED }
