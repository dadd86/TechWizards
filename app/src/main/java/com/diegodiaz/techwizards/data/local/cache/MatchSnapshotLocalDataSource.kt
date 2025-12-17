package com.diegodiaz.techwizards.data.local.cache

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.diegodiaz.techwizards.domain.model.MatchSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Persiste el último estado de un match en DataStore para rehidratación offline.
 */
class MatchSnapshotLocalDataSource(
    private val dataStore: DataStore<Preferences>,
    private val json: Json = Json { encodeDefaults = true }
) {

    fun observar(matchId: String): Flow<MatchSnapshot?> =
        dataStore.data.map { prefs ->
            prefs[key(matchId)]?.let { json.decodeFromString<MatchSnapshotCache>(it).toDomain() }
        }

    suspend fun guardar(matchId: String, snapshot: MatchSnapshot) {
        dataStore.edit { prefs ->
            prefs[key(matchId)] = json.encodeToString(snapshot.toCache())
        }
    }

    private fun key(matchId: String) = stringPreferencesKey("match_snapshot_$matchId")
}

@Serializable
private data class MatchSnapshotCache(
    val match: MatchCache?,
    val participantes: List<MatchParticipantCache>,
    val scores: List<MatchScoreCache>,
    val remotoListo: Boolean,
    val carasElegidas: Map<Long, Int>,
    val lanzamientos: Map<Long, Int>,
    val ganadorRonda: Long?,
    val empate: Boolean
) {
    fun toDomain(): MatchSnapshot = MatchSnapshot(
        match = match?.toDomain(),
        participantes = participantes.map { it.toDomain() },
        scores = scores.map { it.toDomain() },
        remotoListo = remotoListo,
        carasElegidas = carasElegidas,
        lanzamientos = lanzamientos,
        ganadorRonda = ganadorRonda,
        empate = empate
    )
}

@Serializable
private data class MatchCache(
    val id: String,
    val lobbyId: String?,
    val modo: String,
    val estado: String,
    val createdByNumero: Long,
    val createdAtMs: Long,
    val startedAtMs: Long?,
    val finishedAtMs: Long?
)

@Serializable
private data class MatchParticipantCache(
    val matchId: String,
    val usuarioNumero: Long,
    val rol: String,
    val teamId: String?,
    val joinedAtMs: Long?,
    val leftAtMs: Long?,
    val score: Int
)

@Serializable
private data class MatchScoreCache(
    val matchId: String,
    val usuarioNumero: Long,
    val score: Int
)

private fun MatchSnapshot.toCache(): MatchSnapshotCache = MatchSnapshotCache(
    match = match?.toCache(),
    participantes = participantes.map { it.toCache() },
    scores = scores.map { it.toCache() },
    remotoListo = remotoListo,
    carasElegidas = carasElegidas,
    lanzamientos = lanzamientos,
    ganadorRonda = ganadorRonda,
    empate = empate
)

private fun com.diegodiaz.techwizards.domain.model.Match.toCache(): MatchCache = MatchCache(
    id = id,
    lobbyId = lobbyId,
    modo = modo,
    estado = estado.name,
    createdByNumero = createdByNumero,
    createdAtMs = createdAtMs,
    startedAtMs = startedAtMs,
    finishedAtMs = finishedAtMs
)

private fun MatchCache.toDomain(): com.diegodiaz.techwizards.domain.model.Match =
    com.diegodiaz.techwizards.domain.model.Match(
        id = id,
        lobbyId = lobbyId,
        modo = modo,
        estado = com.diegodiaz.techwizards.domain.model.MatchEstado.valueOf(estado),
        createdByNumero = createdByNumero,
        createdAtMs = createdAtMs,
        startedAtMs = startedAtMs,
        finishedAtMs = finishedAtMs
    )

private fun com.diegodiaz.techwizards.domain.model.MatchParticipant.toCache(): MatchParticipantCache =
    MatchParticipantCache(
        matchId = matchId,
        usuarioNumero = usuarioNumero,
        rol = rol,
        teamId = teamId,
        joinedAtMs = joinedAtMs,
        leftAtMs = leftAtMs,
        score = score
    )

private fun MatchParticipantCache.toDomain(): com.diegodiaz.techwizards.domain.model.MatchParticipant =
    com.diegodiaz.techwizards.domain.model.MatchParticipant(
        matchId = matchId,
        usuarioNumero = usuarioNumero,
        rol = rol,
        teamId = teamId,
        joinedAtMs = joinedAtMs,
        leftAtMs = leftAtMs,
        score = score
    )

private fun com.diegodiaz.techwizards.domain.model.MatchScore.toCache(): MatchScoreCache =
    MatchScoreCache(matchId = matchId, usuarioNumero = usuarioNumero, score = score)

private fun MatchScoreCache.toDomain(): com.diegodiaz.techwizards.domain.model.MatchScore =
    com.diegodiaz.techwizards.domain.model.MatchScore(matchId = matchId, usuarioNumero = usuarioNumero, score = score)