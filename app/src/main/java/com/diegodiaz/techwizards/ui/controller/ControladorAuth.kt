package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.core.usecases.CerrarSesionUseCase
import com.diegodiaz.techwizards.core.usecases.IniciarSesionConGoogleUseCase
import com.diegodiaz.techwizards.core.usecases.ObservarUsuarioAutenticadoUseCase
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Controlador de autenticación con Firebase.
 *
 * - Inicia sesión con el token de Google y actualiza [AuthState].
 * - Cierra la sesión activa.
 * - Observa cambios de usuario autenticado.
 * - Permite limpiar mensajes de error.
 */
class ControladorAuth(
    private val iniciarSesion: IniciarSesionConGoogleUseCase,
    private val cerrarSesion: CerrarSesionUseCase,
    private val observarUsuario: ObservarUsuarioAutenticadoUseCase
) : ViewModel() {

    private val _ui = MutableStateFlow(AuthState())
    val ui: StateFlow<AuthState> = _ui.asStateFlow()

    init {
        // Observa cambios de sesión autenticada
        viewModelScope.launch {
            observarUsuario().collectLatest { usuario ->
                _ui.update { current ->
                    current.copy(usuario = usuario)
                }
            }
        }
    }

    /**
     * Inicia sesión con Google usando el idToken recibido desde la UI.
     */
    fun iniciarSesion(idToken: String) {
        if (idToken.isBlank()) {
            _ui.update { it.copy(error = "Token de Google vacío") }
            return
        }

        viewModelScope.launch {
            _ui.update { it.copy(cargando = true, error = null) }

            when (val resultado = iniciarSesion.invoke(idToken)) {
                is Result.Ok -> {
                    _ui.update {
                        it.copy(
                            usuario = resultado.value,
                            cargando = false,
                            error = null
                        )
                    }
                }

                is Result.Err -> {
                    DecentralizedLogger.e(
                        tag = "ControladorAuth",
                        message = "Error al iniciar sesión: ${resultado.error}"
                    )
                    _ui.update {
                        it.copy(
                            cargando = false,
                            error = "No se ha podido iniciar sesión"
                        )
                    }
                }
            }
        }
    }

    /**
     * Cierra la sesión actual y limpia los datos locales relevantes.
     */
    fun cerrarSesion() {
        viewModelScope.launch {
            _ui.update { it.copy(cargando = true, error = null) }

            when (val resultado = cerrarSesion.invoke()) {
                is Result.Ok -> {
                    _ui.update { AuthState() }
                }

                is Result.Err -> {
                    DecentralizedLogger.e(
                        tag = "ControladorAuth",
                        message = "Error al cerrar sesión: ${resultado.error}"
                    )
                    _ui.update {
                        it.copy(
                            cargando = false,
                            error = "No se ha podido cerrar sesión"
                        )
                    }
                }
            }
        }
    }

    /**
     * Limpia el mensaje de error actual en la UI.
     */
    fun limpiarError() {
        _ui.update { it.copy(error = null) }
    }
}


