package com.diegodiaz.techwizards.data.remote.dto

data class LoginResponseDto(
    val token: String,
    val alias: String,
    val isAdmin: Boolean
)