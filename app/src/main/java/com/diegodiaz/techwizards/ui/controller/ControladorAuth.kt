package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegodiaz.techwizards.core.ServiceLocator
import com.diegodiaz.techwizards.domain.model.AuthUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class AuthUiState(
    val usuario: AuthUser? = null,
    val cargando: Boolean = false,
    val error: String? = null
)

class ControladorAuth : ViewModel() {

    private val _ui = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = _ui.asStateFlow()

    // Casos de uso desde el ServiceLocator
    private val iniciarSesionConGoogleUseCase = ServiceLocator.iniciarSesionConGoogleUseCase
    private val cerrarSesionUseCase = ServiceLocator.cerrarSesionUseCase
    private val observarUsuarioAutenticadoUseCase = ServiceLocator.observarUsuarioAutenticadoUseCase

    init {
        // Escuchar siempre el usuario autenticado (cambios desde DataStore)
        viewModelScope.launch {
            observarUsuarioAutenticadoUseCase().collectLatest { user ->
                _ui.value = _ui.value.copy(usuario = user)
            }
        }
    }

    fun iniciarSesionConGoogle(idToken: String) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(cargando = true, error = null)
            try {
                val user = iniciarSesionConGoogleUseCase(idToken)
                _ui.value = _ui.value.copy(usuario = user, cargando = false)
            } catch (t: Throwable) {
                _ui.value = _ui.value.copy(
                    cargando = false,
                    error = t.message ?: "Error al iniciar sesión"
                )
            }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            try {
                cerrarSesionUseCase()
                _ui.value = AuthUiState() // limpia todo
            } catch (t: Throwable) {
                _ui.value = _ui.value.copy(
                    error = t.message ?: "Error al cerrar sesión"
                )
            }
        }
    }

    fun limpiarError() {
        _ui.value = _ui.value.copy(error = null)
    }
}