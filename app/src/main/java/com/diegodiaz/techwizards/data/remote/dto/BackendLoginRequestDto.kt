package com.diegodiaz.techwizards.data.remote.dto

data class BackendLoginRequestDto(
    val firebaseIdToken: String,
    val alias: String? = null, // opcional
)