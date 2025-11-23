package com.diegodiaz.techwizards.ui.controller

import com.diegodiaz.techwizards.domain.model.GameSettings

data class AjustesState (
    val cargando: Boolean = false,
    val errorRes: Int? = null,
    val settings: GameSettings? = null
)
