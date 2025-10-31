package com.diegodiaz.techwizards.data.transaction

/**
 * Ejecuta bloques de código dentro de una transacción.
 *
 * @security Garantiza atomicidad en operaciones que modifican múltiples tablas.
 */
interface TransactionRunner {
    /**
     * Ejecuta el [block] dentro del contexto transaccional configurado.
     *
     * @param block Operaciones a ejecutar de forma atómica.
     * @return Resultado del bloque evaluado.
     */
    suspend operator fun <T> invoke(block: suspend () -> T): T
}
