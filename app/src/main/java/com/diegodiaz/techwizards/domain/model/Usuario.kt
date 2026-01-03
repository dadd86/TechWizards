package com.diegodiaz.techwizards.domain.model

/**
 * Representa el jugador local almacenado en la base de datos.
 *
 * @property numero Identificador interno persistido localmente para la tabla `Usuario`.
 * @property alias Alias público visible por otros jugadores.
 * @property fechaAltaMs Marca de tiempo de registro (epoch millis).
 * @property monedas Saldo actual de monedas virtuales.
 * @property ganoUltimaPartida Indica si la última partida registrada se ganó.
 * @property firebaseUid Identificador remoto opcional vinculado a Firebase Auth.
 * @security
 * - No expone tokens ni correos electrónicos.
 * - Los identificadores deben redactarse antes de escribirse en logs.
 */
data class Usuario(
    val numero: Long,
    val alias: String,
    val fechaAltaMs: Long,
    val monedas: Int,
    val ganoUltimaPartida: Boolean,
    val firebaseUid: String?,
)