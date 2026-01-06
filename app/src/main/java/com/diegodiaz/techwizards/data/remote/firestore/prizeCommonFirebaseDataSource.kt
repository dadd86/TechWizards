package com.diegodiaz.techwizards.data.remote.firestore

import com.diegodiaz.techwizards.domain.model.CommonPrize
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * Fuente de datos del premio común usando Firestore con transacciones.
 *
 * @security
 * - Requiere autenticación a nivel de reglas.
 * - No registra datos sensibles ni tokens.
 */
class PrizeCommonFirebaseDataSource(
    private val firestore: FirebaseFirestore = Firebase.firestore,
) {

    private companion object {
        private const val DEFAULT_DESCRIPTION = "Premio común"
    }

    /**
     * Obtiene el premio común actual desde Firestore.
     *
     * @return Premio común con valor y descripción.
     * @security Lectura pública controlada por reglas de Firestore.
     */
    suspend fun obtenerPremioComun(): CommonPrize {
        val snapshot = prizeDocument().get().await()
        val descripcion = snapshot.getString("descripcion")
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: DEFAULT_DESCRIPTION
        val valor = snapshot.getLong("valor")?.toInt() ?: 0
        val updatedAt = snapshot.getTimestamp("updatedAt")?.toEpochMillis()
        return CommonPrize(
            descripcion = descripcion,
            valor = valor,
            updatedAt = updatedAt
        )
    }

    /**
     * Actualiza el premio común de forma directa.
     *
     * @param premio Nuevo premio.
     * @return Premio común persistido.
     * @security No expone tokens; reglas validan la escritura.
     */
    suspend fun actualizarPremioComun(premio: CommonPrize): CommonPrize {
        require(premio.descripcion.isNotBlank()) { "descripcion vacía" }
        require(premio.valor >= 0) { "valor inválido" }
        prizeDocument().set(
            mapOf(
                "descripcion" to premio.descripcion.trim(),
                "valor" to premio.valor,
                "updatedAt" to FieldValue.serverTimestamp()
            ),
            SetOptions.merge()
        ).await()
        return obtenerPremioComun()
    }

    /**
     * Incrementa el premio común con una transacción atómica.
     *
     * @param delta Incremento positivo.
     * @return Premio común actualizado.
     * @security Operación atómica, sin exponer datos sensibles.
     */
    suspend fun incrementarPremioComun(delta: Int): CommonPrize {
        require(delta > 0) { "delta debe ser > 0" }
        val transactionResult = firestore.runTransaction { tx ->
            val snapshot = tx.get(prizeDocument())
            val descripcion = snapshot.getString("descripcion")
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?: DEFAULT_DESCRIPTION
            val current = snapshot.getLong("valor") ?: 0L
            val nuevoValor = current + delta
            tx.set(
                prizeDocument(),
                mapOf(
                    "valor" to nuevoValor,
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            )
            CommonPrize(
                descripcion = descripcion,
                valor = nuevoValor.toInt(),
                updatedAt = null
            )
        }.await()
        return transactionResult
    }

    /**
     * Reclama el premio común y lo resetea a 0 de forma atómica.
     *
     * @param firebaseUid UID autenticado de Firebase.
     * @param claimId Identificador idempotente del cobro.
     * @return Total cobrado del premio común.
     * @security Opera dentro de la cuenta autenticada.
     */
    suspend fun reclamarPremioComun(firebaseUid: String, claimId: String): Int {
        require(firebaseUid.isNotBlank()) { "firebaseUid vacío" }
        require(claimId.isNotBlank()) { "claimId vacío" }

        return firestore.runTransaction { tx ->
            val prizeRef = prizeDocument()
            val playerRef = playerDocument(firebaseUid)
            val historyRef = playerRef.collection("history").document(claimId)

            val premio = (tx.get(prizeRef).getLong("valor") ?: 0L).toInt()
            if (premio <= 0) return@runTransaction 0

            tx.set(
                prizeRef,
                mapOf(
                    "valor" to 0,
                    "updatedAt" to FieldValue.serverTimestamp(),
                    "lastWinnerUid" to firebaseUid,
                    "lastClaimId" to claimId,
                    "lastClaimAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            )

            if (premio > 0) {
                tx.update(
                    playerRef,
                    "coins",
                    FieldValue.increment(premio.toLong())
                )
                tx.set(
                    historyRef,
                    mapOf(
                        "fecha" to FieldValue.serverTimestamp(),
                        "resultado" to "GANADO",
                        "deltaMonedas" to premio,
                        "premioComunCobrado" to premio
                    )
                )
            }

            premio
        }.await()
    }

    private fun prizeDocument() = firestore.collection("prize").document("common")

    private fun playerDocument(firebaseUid: String) =
        firestore.collection("players").document(firebaseUid)

    private fun Timestamp.toEpochMillis(): Long = toDate().time
}
