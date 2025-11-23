package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.domain.model.GameSettings
import com.diegodiaz.techwizards.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class ObservarPreferenciasUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<GameSettings> = settingsRepository.observarPreferencias()
}