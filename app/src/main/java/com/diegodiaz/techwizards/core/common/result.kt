package com.diegodiaz.techwizards.core.common

/**
 * Resultado genérico tipado para operaciones del dominio.
 *
 * @param T Tipo de éxito.
 * @param E Tipo de error.
 * @security
 * - Evita exponer excepciones sin sanitizar en capas superiores.
 */
sealed class Result<out T, out E> {
    /** Representa un éxito con valor [value]. */
    data class Ok<T>(val value: T) : Result<T, Nothing>()

    /** Representa un fallo con información de error [error]. */
    data class Err<E>(val error: E) : Result<Nothing, E>()
}