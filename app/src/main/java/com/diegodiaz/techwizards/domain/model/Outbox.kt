package com.diegodiaz.techwizards.domain.model

//Operación idempotente pendiente de sincronizar con el backend remoto.

data class Outbox(
    val id: Long,
    val tipo: String,
    val payload: String,
    val creadoEn: Long = System.currentTimeMillis(),
    val entregado: Boolean = false,
    val reintentos: Int = 0
)
