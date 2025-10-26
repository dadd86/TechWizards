package com.diegodiaz.techwizards.core.common

/**
 * Modela fallos conocidos en la arquitectura del juego para trazabilidad uniforme.
 *
 * @security
 * - No incluye detalles internos de infraestructura que puedan ser explotados.
 */
sealed class AgentError {
    /** Error asociado a la red o capa remota. */
    data object Network : AgentError()

    /** Operación cancelada por timeout explícito. */
    data object Timeout : AgentError()

    /** Error de validación de entrada de usuario o regla de negocio. */
    data class Validation(val reason: String) : AgentError()

    /** Error originado en la base de datos local. */
    data class Database(val cause: Throwable) : AgentError()

    /** Error inesperado que debe auditarse. */
    data class Unknown(val cause: Throwable?) : AgentError()
}