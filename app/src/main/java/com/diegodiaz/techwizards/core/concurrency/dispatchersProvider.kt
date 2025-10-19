package com.diegodiaz.techwizards.core.concurrency

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Define los `Dispatchers` usados en corrutinas críticas.
 *
 * @security
 * - Centraliza los hilos para evitar bloqueos en el hilo principal.
 * - Permite intercambiar implementaciones en tests y así limitar efectos colaterales.
 */
interface dispatchersProveedor {
    /**
     * Dispatcher para operaciones de I/O (Room, red, ficheros).
     */
    val io: CoroutineDispatcher

    /**
     * Dispatcher para trabajo intensivo en CPU.
     */
    val default: CoroutineDispatcher

    /**
     * Dispatcher vinculado al hilo principal de Android.
     */
    val main: CoroutineDispatcher

    companion object {
        /**
         * Implementación por defecto que usa los `Dispatchers` estándar.
         *
         * @security Garantiza que Room y red se ejecutan fuera del hilo principal.
         */
        val porDefecto: dispatchersProveedor = object : dispatchersProveedor {
            override val io: CoroutineDispatcher get() = Dispatchers.IO
            override val default: CoroutineDispatcher get() = Dispatchers.Default
            override val main: CoroutineDispatcher get() = Dispatchers.Main
        }
    }
}