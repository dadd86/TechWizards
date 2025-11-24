package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.diegodiaz.techwizards.domain.model.GameSettings
import com.diegodiaz.techwizards.domain.model.gameSettingsDefault
import kotlinx.coroutines.launch
import android.net.Uri
import com.diegodiaz.techwizards.core.usecases.ActualizarPreferenciasUseCase
import com.diegodiaz.techwizards.core.usecases.ObtenerPreferenciasUseCase
import com.diegodiaz.techwizards.core.usecases.ObservarPreferenciasUseCase
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger

/**
 * Controlador (ViewModel) para la pantalla de ajustes.
 *
 * @param obtenerPreferencias Caso de uso para leer preferencias locales.
 * @param actualizarPreferencias Caso de uso que persiste cambios de configuración.
 * @param observarPreferencias Flujo reactivo con los ajustes actuales.
 * @return `ViewModel` listo para exponer `StateFlow` a la UI.
 * @throws IllegalStateException No se lanza directamente; excepciones de flujos se propagan.
 * @security No registra PII; los logs solo informan cambios de flags sin datos sensibles.
 */
class ControladorAjustes(
    private val obtenerPreferencias: ObtenerPreferenciasUseCase,
    private val actualizarPreferencias: ActualizarPreferenciasUseCase,
    private val observarPreferencias: ObservarPreferenciasUseCase

) : ViewModel() {
        private val _ui = MutableStateFlow(
            AjustesState(
                cargando = false,
                errorRes = null,
                settings = gameSettingsDefault
            )
        )

        val ui: StateFlow<AjustesState> = _ui

        init {
            inicializarPreferencias()
        }

        /**
         * Carga el estado inicial de preferencias y se suscribe a sus cambios.
         *
         * @return `Unit` tras iniciar la lectura y observación.
         * @throws IllegalStateException No se lanza; errores de flujo se propagan en colecta.
         * @security No expone datos sensibles; solo banderas de configuración.
         */
        private fun inicializarPreferencias() {
            viewModelScope.launch {
                val lectura = obtenerPreferencias()
                val base = when (lectura) {
                    is Result.Ok -> lectura.value
                    is Result.Err -> {
                        DecentralizedLogger.w(
                            tag = "AjustesViewModel",
                            message = "Preferencias no disponibles; se usará el valor por defecto"
                        )
                        gameSettingsDefault
                    }
                }
                _ui.update { it.copy(settings = base, cargando = false, errorRes = null) }
                observarPreferencias().collect { nuevosSettings ->
                    DecentralizedLogger.d("AjustesViewModel", "Preferencias actualizadas en flujo")
                    _ui.update { it.copy(settings = nuevosSettings, cargando = false, errorRes = null) }
                }
            }
        }

        /**
         * Alterna el tema oscuro en configuración.
         *
         * @param enabled Indica si se activa el tema oscuro.
         * @return `Unit` tras programar la persistencia.
         * @throws IllegalStateException No se lanza; las excepciones del flujo se propagan.
         * @security No registra preferencias de usuario más allá del flag.
         */
        fun actualizarTemaOscuro(enabled: Boolean) {
            actualizarYGuardar { it.copy(darkThemeEnabled = enabled) }
        }

        /**
         * Actualiza el estado de la música de fondo.
         *
         * @param enabled `true` si la música debe sonar.
         * @return `Unit` tras despachar el guardado.
         * @throws IllegalStateException No se lanza.
         * @security No guarda información de pistas; solo el flag.
         */
        fun actualizarMusica(enabled: Boolean) {
            actualizarYGuardar { it.copy(musicEnabled = enabled) }
        }

        /**
         * Actualiza el estado de los efectos de sonido.
         *
         * @param enabled `true` para habilitarlos.
         * @return `Unit` tras persistir.
         * @throws IllegalStateException No se lanza.
         * @security Solo registra el cambio en logs generales.
         */
        fun actualizarSfx(enabled: Boolean) {
            actualizarYGuardar { it.copy(sfxEnabled = enabled) }
        }

        /**
         * Alterna las animaciones de fichas.
         *
         * @param enabled Si las animaciones deben mostrarse.
         * @return `Unit` tras guardar.
         * @throws IllegalStateException No se lanza.
         * @security No incluye PII en logs.
         */
        fun actualizarAnimaciones(enabled: Boolean) {
            actualizarYGuardar { it.copy(animationsEnabled = enabled) }
        }

        /**
         * Activa o desactiva notificaciones de victoria.
         *
         * @param enabled Si las notificaciones deben enviarse.
         * @return `Unit` tras persistir la preferencia.
         * @throws IllegalStateException No se lanza.
         * @security No registra detalles de usuario.
         */
        fun actualizarNotificaciones(enabled: Boolean) {
            actualizarYGuardar { it.copy(notificationsEnabled = enabled) }
        }

        /**
         * Selecciona una pista personalizada para la música de fondo.
         *
         * @param uri URI seleccionada o `null` para volver a la pista oficial.
         * @return `Unit` tras persistir la selección.
         * @throws IllegalStateException No se lanza.
         * @security No se almacena ruta en logs; solo se persiste en preferencias.
         */
        fun seleccionarPista(uri: Uri?) {
            actualizarYGuardar { it.copy(selectedMusicUri = uri?.toString()) }
        }

        /**
         * Cambia el idioma preferido de la aplicación.
         *
         * @param tag Etiqueta BCP47 seleccionada.
         * @return `Unit` tras guardar.
         * @throws IllegalStateException No se lanza.
         * @security No registra idiomas anteriores.
         */
        fun actualizarIdioma(tag: String) {
            actualizarYGuardar { it.copy(selectedLanguageTag = tag) }
        }

        private fun actualizarYGuardar(modificar: (GameSettings) -> GameSettings) {
            val prev = _ui.value.settings ?: gameSettingsDefault
            val modificado = modificar(prev)
            _ui.update { it.copy(settings = modificado) }
            viewModelScope.launch {
                DecentralizedLogger.i("AjustesViewModel", "Persistiendo cambios de ajustes")
                actualizarPreferencias(modificado)
            }
        }
    }