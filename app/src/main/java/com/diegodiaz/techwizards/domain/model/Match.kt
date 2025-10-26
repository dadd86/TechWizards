package com.diegodiaz.techwizards.domain.model

data class Match(
    val id: Long,
    val lobbyId: Long? = null,
    val status: Status = Status.CREATED,
    val inicioEn: Long = System.currentTimeMillis(),
    val finEn: Long? = null
) {
    enum class Status { CREATED, RUNNING, FINISHED, CANCELLED }
}
