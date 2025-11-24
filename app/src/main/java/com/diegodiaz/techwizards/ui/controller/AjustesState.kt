package com.diegodiaz.techwizards.ui.controller

import com.diegodiaz.techwizards.domain.model.GameSettings
import com.diegodiaz.techwizards.domain.model.gameSettingsDefault

data class AjustesState (
    val cargando: Boolean = false,
    val errorRes: Int? = null,
    val settings: GameSettings = gameSettingsDefault
)
