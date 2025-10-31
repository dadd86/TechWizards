package com.diegodiaz.techwizards.data.result


/**
 * Errores conocidos al interactuar con la capa de datos.
 */
sealed class DataError {
    /** No se encontró la entidad solicitada. */
    data object NotFound : DataError()

    /** Violación de reglas de negocio o entradas inválidas. */
    data class Validation(val reason: String) : DataError()

    /** Error producido por Room u otra infraestructura local. */
    data class Database(val cause: Throwable) : DataError()

    /** Error inesperado que debe investigarse. */
    data class Unknown(val cause: Throwable?) : DataError()
}