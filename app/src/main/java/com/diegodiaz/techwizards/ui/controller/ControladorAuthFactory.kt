package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diegodiaz.techwizards.core.usecases.CerrarSesionUseCase
import com.diegodiaz.techwizards.core.usecases.IniciarSesionConGoogleUseCase
import com.diegodiaz.techwizards.core.usecases.ObservarUsuarioAutenticadoUseCase

/**
 * Factory simple para crear instancia de [ControladorAuth] con sus use cases.
 */
class ControladorAuthFactory(
    private val iniciarSesion: IniciarSesionConGoogleUseCase,
    private val cerrarSesion: CerrarSesionUseCase,
    private val observarUsuario: ObservarUsuarioAutenticadoUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ControladorAuth::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ControladorAuth(
                iniciarSesion = iniciarSesion,
                cerrarSesion = cerrarSesion,
                observarUsuario = observarUsuario
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
