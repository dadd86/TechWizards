package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.diegodiaz.techwizards.domain.model.GameSettings
import kotlinx.coroutines.launch
import android.net.Uri
import com.diegodiaz.techwizards.core.usecases.*


class ControladorAjustes(
    private val obtenerPreferencias: ObtenerPreferenciasUseCase,
    private val actualizarPreferencias: ActualizarPreferenciasUseCase,
    private val observarPreferencias: ObservarPreferenciasUseCase
) : ViewModel () {

    // Valor por defecto para los settings
    val GAME_SETTINGS_DEFAULT = GameSettings(
        musicEnabled = true,
        sfxEnabled = true,
        darkThemeEnabled = false,
        animationsEnabled = true,
        notificationsEnabled = true,
        selectedMusicUri = null,
        selectedLanguageTag = "es-ES"
    )
    private val _ui = MutableStateFlow(
        AjustesState(
            cargando = false,
            errorRes = null,
            settings = GAME_SETTINGS_DEFAULT
        )
    )

    val ui: StateFlow<AjustesState> = _ui

    init {
        viewModelScope.launch {
            observarPreferencias().collect { nuevosSettings ->
                _ui.update { it.copy(settings = nuevosSettings, cargando = false, errorRes = null) }
            }
        }
    }

    // Métodos para manejar los eventos de cambio en los settings
    fun actualizarTemaOscuro(enabled: Boolean) {
        actualizarYGuardar { it.copy(darkThemeEnabled = enabled) }
    }

    fun actualizarMusica(enabled: Boolean) {
        actualizarYGuardar { it.copy(musicEnabled = enabled) }
    }

    fun actualizarSfx(enabled: Boolean) {
        actualizarYGuardar { it.copy(sfxEnabled = enabled) }
    }

    fun actualizarAnimaciones(enabled: Boolean) {
        actualizarYGuardar { it.copy(animationsEnabled = enabled) }
    }

    fun actualizarNotificaciones(enabled: Boolean) {
        actualizarYGuardar { it.copy(notificationsEnabled = enabled) }
    }

    fun seleccionarPista(uri: Uri?) {
        actualizarYGuardar { it.copy(selectedMusicUri = uri?.toString()) }
    }

    fun actualizarIdioma(tag: String) {
        actualizarYGuardar { it.copy(selectedLanguageTag = tag) }
    }

    private fun actualizarYGuardar(modificar: (GameSettings) -> GameSettings) {
        val prev = _ui.value.settings ?: GAME_SETTINGS_DEFAULT
        val modificado = modificar(prev)
        _ui.update { it.copy(settings = modificado) }
        // Llama al UseCase para persistir asíncronamente
        viewModelScope.launch { actualizarPreferencias(modificado) }
    }
}
