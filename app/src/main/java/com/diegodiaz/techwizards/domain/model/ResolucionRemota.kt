package com.diegodiaz.techwizards.domain.model

/**
 * Representa una apuesta remota enviada por el backend.
 *
 * @property usuarioId Identificador del usuario que apostó.
 * @property monto Cantidad de monedas involucradas en la apuesta.
 * @property numeroElegido Cara seleccionada por el jugador, si la mecánica lo requiere.
 * @security
 * - No contiene datos sensibles adicionales más allá de identificadores públicos.
 */
data class ApuestaRemota(
    val usuarioId: String,
    val monto: Int,
    val numeroElegido: Int?,
)

/**
 * Payload de resolución de tirada recibido desde el backend.
 *
 * @property rolledFace Cara del dado que salió en el servidor.
 * @property apuestas Listado de apuestas realizadas por los usuarios.
 * @property winnerUserId Identificador del ganador declarado por el servidor.
 * @security
 * - Los identificadores deben redactarse antes de escribirlos en logs externos.
 */
data class ResolucionTiradaRemota(
    val rolledFace: Int,
    val apuestas: List<ApuestaRemota>,
    val winnerUserId: String?,
)

/**
 * Resultado calculado tras aplicar la resolución remota sobre el estado local.
 *
 * @property partida Registro persistido en historial.
 * @property rolledFace Cara obtenida.
 * @property gano Indica si el usuario local ganó.
 * @property deltaMonedas Variación aplicada al monedero local.
 */
data class ResolucionTiradaResultado(
    val partida: Partida,
    val rolledFace: Int,
    val gano: Boolean,
    val deltaMonedas: Int,
)