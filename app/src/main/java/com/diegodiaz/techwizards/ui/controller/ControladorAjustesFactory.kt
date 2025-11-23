package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diegodiaz.techwizards.core.usecases.ActualizarPreferenciasUseCase
import com.diegodiaz.techwizards.core.usecases.ObservarPreferenciasUseCase
import com.diegodiaz.techwizards.core.usecases.ObtenerPreferenciasUseCase

class ControladorAjustesFactory(
    private val obtenerPreferencias: ObtenerPreferenciasUseCase,
    private val actualizarPreferencias: ActualizarPreferenciasUseCase,
    private val observarPreferencias: ObservarPreferenciasUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ControladorAjustes::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ControladorAjustes(
                obtenerPreferencias,
                actualizarPreferencias,
                observarPreferencias
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}