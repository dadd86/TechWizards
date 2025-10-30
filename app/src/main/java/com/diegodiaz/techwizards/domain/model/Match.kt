package com.diegodiaz.techwizards.domain.model


/**
 * Representa una instancia de partida multijugador.
 *
 * @property id Identificador global (UUID/ULID).
 * @property lobbyId Identificador del lobby de origen, si aplica.
 * @property modo Modo de juego utilizado.
 * @property estado Estado actual de la partida.
 * @property createdByNumero Usuario host que creó la partida.
 * @property createdAtMs Marca temporal de creación.
 * @property startedAtMs Marca de inicio si ya se lanzó.
 * @property finishedAtMs Marca de fin si finalizó.
 * @security
 * - No expone PII; únicamente identificadores técnicos.
 */
data class Match(
    val id: String,
    val lobbyId: String?,
    val modo: String,
    val estado: MatchEstado,
    val createdByNumero: Long,
    val createdAtMs: Long,
    val startedAtMs: Long?,
    val finishedAtMs: Long?,
)

/**
 * Estados válidos del match según la tabla `Match`.
 *
 * @security Restringe a valores conocidos para prevenir datos corruptos.
 */
enum class MatchEstado { PENDING, ACTIVE, FINISHED, CANCELLED }