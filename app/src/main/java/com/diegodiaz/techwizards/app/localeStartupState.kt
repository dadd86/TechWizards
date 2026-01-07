package com.diegodiaz.techwizards.app

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Estado mínimo para coordinar el arranque y evitar un primer frame vacío.
 *
 * @security No almacena datos sensibles, solo una bandera de inicialización.
 */
object LocaleStartupState {
    private val ready = AtomicBoolean(false)

    /** Indica si el locale ya fue aplicado o confirmado. */
    val isReady: Boolean
        get() = ready.get()

    /** Marca el estado como listo. */
    fun markReady() {
        ready.set(true)
    }
}