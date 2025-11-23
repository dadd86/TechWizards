package com.diegodiaz.techwizards.data.repository.impl

import android.content.Context
import com.diegodiaz.techwizards.domain.model.GameSettings
import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryDataStore(
    private val context: Context
) : SettingsRepository {
    private val flow = MutableStateFlow(
        GameSettings(
            musicEnabled = true,
            sfxEnabled = true,
            darkThemeEnabled = false,
            animationsEnabled = true,
            notificationsEnabled = true,
            selectedMusicUri = null,
            selectedLanguageTag = "es-ES"
        )
    )

    override suspend fun guardarPreferencias(settings: GameSettings): Result<Unit, AgentError> {
        flow.value = settings
        return Result.Ok(Unit)
    }

    override suspend fun obtenerPreferencias(): Result<GameSettings, AgentError> {
        return Result.Ok(flow.value)
    }

    override fun observarPreferencias(): Flow<GameSettings> = flow.asStateFlow()
}