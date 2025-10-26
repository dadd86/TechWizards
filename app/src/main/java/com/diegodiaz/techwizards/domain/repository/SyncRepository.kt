package com.diegodiaz.techwizards.domain.repository

import io.reactivex.rxjava3.core.Completable

/**
 * SyncRepository.kt
 *
 * Gestiona la sincronización de datos entre la base local y un posible servidor.
 * En el contexto actual, se usa principalmente para limpiar o reiniciar datos.
 *
 * 🔹 Define QUÉ acciones puede hacer el dominio a nivel global.
 * 🔹 La implementación (SyncRepositoryRoom.kt) define CÓMO se ejecuta en Room.
 */
interface SyncRepository {

    // 🔹 RxJava — versión reactiva
    fun limpiarTodoRx(): Completable

    // 🔹 Coroutines — versión suspendida
    suspend fun limpiarTodo()
}
