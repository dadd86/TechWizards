package com.diegodiaz.techwizards.data.remote.prize

import com.diegodiaz.techwizards.data.remote.score.PrizeClaimRequestDto
import com.diegodiaz.techwizards.data.remote.score.PrizeIncrementRequestDto
import com.diegodiaz.techwizards.data.remote.score.ScoreApi
import com.diegodiaz.techwizards.data.remote.score.toDomain
import com.diegodiaz.techwizards.data.remote.score.toRequestDto
import com.diegodiaz.techwizards.domain.model.CommonPrize
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

/**
 * Fuente HTTP para administrar el premio común global vía ScoreApi.
 *
 * @param scoreApi Cliente REST hacia el backend de puntuaciones.
 * @param pollingIntervalMs Intervalo de refresco para observar el premio común.
 * @security
 * - El cliente no escribe Firestore directamente para el premio común.
 * - Todas las mutaciones pasan por el backend autenticado (Bearer).
 */
class PremioComunBackendDataSource(
    private val scoreApi: ScoreApi,
    private val pollingIntervalMs: Long = 5_000L,
) {

    /**
     * Obtiene el premio común actual desde el backend.
     *
     * @param bearerToken Token Bearer opcional para endpoints protegidos.
     * @return Premio común con descripción y valor.
     * @throws Exception Si falla la llamada HTTP al backend.
     * @security No persiste ni registra tokens.
     */
    suspend fun obtenerPremioComun(bearerToken: String?): CommonPrize {
        return scoreApi.fetchCommonPrize(bearerToken = bearerToken).toDomain()
    }

    /**
     * Observa el premio común consultando periódicamente el backend.
     *
     * @param bearerTokenProvider Proveedor de token Bearer opcional.
     * @return Flujo con el premio común actualizado.
     * @throws Exception Si falla la llamada HTTP al backend.
     * @security No persiste ni registra tokens.
     */
    fun observarPremioComun(bearerTokenProvider: () -> String?): Flow<CommonPrize> = flow {
        while (currentCoroutineContext().isActive) {
            val bearer = bearerTokenProvider()
            emit(scoreApi.fetchCommonPrize(bearerToken = bearer).toDomain())
            delay(pollingIntervalMs)
        }
    }

    /**
     * Actualiza el premio común (descripción y valor) vía backend.
     *
     * @param bearerToken Token Bearer requerido para escritura.
     * @param nuevoPremio Datos validados del premio común.
     * @return Premio común persistido en backend.
     * @throws IllegalArgumentException Si los datos son inválidos.
     * @throws Exception Si falla la llamada HTTP al backend.
     * @security Requiere autenticación con Bearer.
     */
    suspend fun actualizarPremioComun(bearerToken: String, nuevoPremio: CommonPrize): CommonPrize {
        require(nuevoPremio.descripcion.isNotBlank()) { "descripcion vacía" }
        require(nuevoPremio.valor >= 0) { "valor negativo" }
        return scoreApi.updatePrize(
            bearerToken = bearerToken,
            request = nuevoPremio.toRequestDto()
        ).toDomain()
    }

    /**
     * Incrementa el premio común por una derrota.
     *
     * @param bearerToken Token Bearer requerido para escritura.
     * @param delta Incremento positivo.
     * @return Premio común con el valor actualizado.
     * @throws IllegalArgumentException Si delta no es positivo.
     * @throws Exception Si falla la llamada HTTP al backend.
     * @security Requiere autenticación con Bearer.
     */
    suspend fun incrementarPremioComun(bearerToken: String, delta: Int): CommonPrize {
        require(delta > 0) { "delta debe ser > 0" }
        return scoreApi.incrementCommonPrize(
            bearerToken = bearerToken,
            request = PrizeIncrementRequestDto(delta = delta)
        ).toDomain()
    }

    /**
     * Reclama el premio común y lo resetea a cero en el backend.
     *
     * @param bearerToken Token Bearer requerido para escritura.
     * @param claimId Identificador de reclamo (auditoría).
     * @return Valor del premio común cobrado.
     * @throws IllegalArgumentException Si claimId está vacío.
     * @throws Exception Si falla la llamada HTTP al backend.
     * @security Requiere autenticación con Bearer.
     */
    suspend fun reclamarPremioComun(bearerToken: String, claimId: String): Int {
        require(claimId.isNotBlank()) { "claimId vacío" }
        val response = scoreApi.claimCommonPrize(
            bearerToken = bearerToken,
            request = PrizeClaimRequestDto(claimId = claimId)
        )
        return response.claimed
    }
}