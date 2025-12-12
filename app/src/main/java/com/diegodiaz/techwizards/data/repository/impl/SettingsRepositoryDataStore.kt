package com.diegodiaz.techwizards.data.repository.impl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.GameSettings
import com.diegodiaz.techwizards.domain.model.gameSettingsDefault
import com.diegodiaz.techwizards.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "game_settings")

class SettingsRepositoryDataStore(
    private val context: Context
) : SettingsRepository {

    val dataStore: androidx.datastore.core.DataStore<Preferences>
        get() = context.settingsDataStore

    private companion object {
        val musicEnabledKey = booleanPreferencesKey("music_enabled")
        val sfxEnabledKey = booleanPreferencesKey("sfx_enabled")
        val darkThemeKey = booleanPreferencesKey("dark_theme_enabled")
        val animationsKey = booleanPreferencesKey("animations_enabled")
        val notificationsKey = booleanPreferencesKey("notifications_enabled")
        val musicUriKey = stringPreferencesKey("selected_music_uri")
        val languageTagKey = stringPreferencesKey("selected_language_tag")
    }

    override suspend fun guardarPreferencias(settings: GameSettings): Result<Unit, AgentError> =
        try {
            context.settingsDataStore.edit { prefs ->
                prefs[musicEnabledKey] = settings.musicEnabled
                prefs[sfxEnabledKey] = settings.sfxEnabled
                prefs[darkThemeKey] = settings.darkThemeEnabled
                prefs[animationsKey] = settings.animationsEnabled
                prefs[notificationsKey] = settings.notificationsEnabled
                prefs[musicUriKey] = settings.selectedMusicUri ?: ""
                prefs[languageTagKey] = settings.selectedLanguageTag
            }
            Result.Ok(Unit)
        } catch (t: Throwable) {
            Result.Err(AgentError.Unknown(t))
        }

    override suspend fun obtenerPreferencias(): Result<GameSettings, AgentError> =
        try {
            val prefs = context.settingsDataStore.data.first()
            Result.Ok(prefs.toGameSettings())
        } catch (t: Throwable) {
            Result.Err(AgentError.Unknown(t))
        }

    override fun observarPreferencias(): Flow<GameSettings> =
        context.settingsDataStore.data
            .map { prefs -> prefs.toGameSettings() }
            .catch { emit(gameSettingsDefault) }

    private fun Preferences.toGameSettings(): GameSettings = GameSettings(
        musicEnabled = this[musicEnabledKey] ?: gameSettingsDefault.musicEnabled,
        sfxEnabled = this[sfxEnabledKey] ?: gameSettingsDefault.sfxEnabled,
        darkThemeEnabled = this[darkThemeKey] ?: gameSettingsDefault.darkThemeEnabled,
        animationsEnabled = this[animationsKey] ?: gameSettingsDefault.animationsEnabled,
        notificationsEnabled = this[notificationsKey] ?: gameSettingsDefault.notificationsEnabled,
        selectedMusicUri = this[musicUriKey]?.ifEmpty { null },
        selectedLanguageTag = this[languageTagKey] ?: gameSettingsDefault.selectedLanguageTag
    )
    }