package com.diegodiaz.techwizards.domain.model

/**
 * Mensaje del chat en tiempo real dentro de un match.
 *
 * @property id Identificador global del mensaje.
 * @property matchId Partida a la que pertenece.
 * @property senderNumero Número del usuario emisor.
 * @property text Contenido sanitizado del mensaje.
 * @property createdAtMs Marca temporal de envío (epoch millis).
 * @security
 * - El texto debe estar sanitizado y filtrado contra contenido inapropiado.
 */
data class Message(
    val id: String,
    val matchId: String,
    val senderNumero: Long,
    val text: String,
    val createdAtMs: Long,
)