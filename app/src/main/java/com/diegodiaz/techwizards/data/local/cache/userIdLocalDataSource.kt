package com.diegodiaz.techwizards.data.local.cache

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Gestiona la persistencia del identificador local del usuario en DataStore.
 *
 * @security No registra valores sensibles ni expone el identificador en logs.
 */
class UserIdLocalDataSource(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val usuarioIdKey = stringPreferencesKey("usuario_id")
    }

    /**
     * Obtiene el ID persistido si existe.
     *
     * @return ID del usuario o null si no fue persistido.
     * @security No registra valores sensibles ni expone el identificador en logs.
     */
    suspend fun obtenerUsuarioId(): String? =
        dataStore.data.first()[usuarioIdKey]

    /**
     * Observa cambios del ID persistido.
     *
     * @return Flujo con el ID del usuario o null si no existe.
     * @security No registra valores sensibles ni expone el identificador en logs.
     */
    fun observarUsuarioId(): Flow<String?> =
        dataStore.data.map { prefs -> prefs[usuarioIdKey] }

    /**
     * Persiste el ID del usuario.
     *
     * @param usuarioId Identificador local del jugador.
     * @security No registra valores sensibles ni expone el identificador en logs.
     */
    suspend fun guardarUsuarioId(usuarioId: String) {
        dataStore.edit { prefs ->
            prefs[usuarioIdKey] = usuarioId
        }
    }
}