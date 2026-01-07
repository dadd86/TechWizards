package com.diegodiaz.techwizards.data.remote.prize

import com.diegodiaz.techwizards.domain.model.CommonPrize
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Fuente Firestore para administrar el premio común global.
 *
 * @param firestore Cliente Firestore para transacciones atómicas.
 * @param firebaseAuth Acceso a UID autenticado para auditoría del premio.
 * @security No registra PII ni tokens; solo usa UID para trazabilidad.
 */
class PremioComunFirestoreDataSource(
    private val firestore: FirebaseFirestore = Firebase.firestore,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
) {

    private val prizeRef = firestore.document("prize/common")

    /**
     * Obtiene el premio común actual desde Firestore.
     *
     * @return Premio común con descripción y valor.
     * @throws Exception Si falla la lectura remota.
     * @security No persiste tokens ni datos sensibles.
     */
    suspend fun obtenerPremioComun(): CommonPrize {
        val snapshot = prizeRef.get().await()
        return snapshot.toCommonPrize()
    }

    /**
     * Observa el premio común en tiempo real.
     *
     * @return Flujo con el premio común actualizado.
     * @security No expone PII; solo descripción y valor.
     */
    fun observarPremioComun(): Flow<CommonPrize> = callbackFlow {
        val listener = prizeRef.addSnapshotListener { snapshot, error ->
            error?.let { close(it) }
            if (snapshot != null) {
                trySend(snapshot.toCommonPrize())
            }
        }
        awaitClose { listener.remove() }
    }

    /**
     * Actualiza el premio común (descripción y valor) en Firestore.
     *
     * @param nuevoPremio Datos del premio común validados.
     * @return Premio común persistido.
     * @throws IllegalArgumentException Si los datos son inválidos.
     * @security Requiere reglas de Firestore para acceso admin si aplica.
     */
    suspend fun actualizarPremioComun(nuevoPremio: CommonPrize): CommonPrize {
        require(nuevoPremio.descripcion.isNotBlank()) { "descripcion vacía" }
        require(nuevoPremio.valor >= 0) { "valor negativo" }
        val uid = firebaseAuth.currentUser?.uid?.trim()
        val payload = mutableMapOf<String, Any>(
            "descripcion" to nuevoPremio.descripcion.trim(),
            "valor" to nuevoPremio.valor,
            "updatedAt" to FieldValue.serverTimestamp(),
        )
        if (!uid.isNullOrBlank()) {
            payload["updatedByUid"] = uid
        }
        prizeRef.set(payload, com.google.firebase.firestore.SetOptions.merge()).await()
        return nuevoPremio
    }

    /**
     * Incrementa el premio común por una derrota.
     *
     * @param delta Incremento positivo.
     * @return Premio común con valor actualizado.
     * @throws IllegalArgumentException Si delta no es positivo.
     * @security Usa transacción para atomicidad.
     */
    suspend fun incrementarPremioComun(delta: Int): CommonPrize {
        require(delta > 0) { "delta debe ser > 0" }
        val uid = firebaseAuth.currentUser?.uid?.trim()
        return firestore.runTransaction { transaction ->
            val snapshot = transaction.get(prizeRef)
            val current = (snapshot.getLong("valor") ?: 0L).toInt()
            val descripcion = snapshot.getString("descripcion") ?: "Premio común"
            val next = current + delta
            val payload = mutableMapOf<String, Any>(
                "descripcion" to descripcion,
                "valor" to next,
                "updatedAt" to FieldValue.serverTimestamp(),
            )
            if (!uid.isNullOrBlank()) {
                payload["updatedByUid"] = uid
            }
            transaction.set(prizeRef, payload, com.google.firebase.firestore.SetOptions.merge())
            CommonPrize(descripcion = descripcion, valor = next)
        }.await()
    }

    /**
     * Reclama el premio común y lo resetea a cero.
     *
     * @param claimId Identificador idempotente del reclamo.
     * @return Valor del premio común cobrado.
     * @throws IllegalArgumentException Si claimId está vacío.
     * @security Usa transacción para garantizar que solo un ganador cobra.
     */
    suspend fun reclamarPremioComun(claimId: String): Int {
        require(claimId.isNotBlank()) { "claimId vacío" }
        val uid = firebaseAuth.currentUser?.uid?.trim()
        return firestore.runTransaction { transaction ->
            val snapshot = transaction.get(prizeRef)
            val dataClaimId = snapshot.getString("lastClaimId").orEmpty()
            val lastClaimAmount = (snapshot.getLong("lastClaimAmount") ?: 0L).toInt()
            if (dataClaimId.isNotBlank() && dataClaimId == claimId) {
                return@runTransaction lastClaimAmount
            }
            val currentPrize = (snapshot.getLong("valor") ?: 0L).toInt()
            val descripcion = snapshot.getString("descripcion") ?: "Premio común"
            val payload = mutableMapOf<String, Any>(
                "descripcion" to descripcion,
                "valor" to 0,
                "lastClaimId" to claimId,
                "lastClaimAmount" to currentPrize,
                "lastClaimedAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp(),
            )
            if (!uid.isNullOrBlank()) {
                payload["lastClaimedByUid"] = uid
                payload["updatedByUid"] = uid
            }
            transaction.set(prizeRef, payload, com.google.firebase.firestore.SetOptions.merge())
            currentPrize
        }.await()
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toCommonPrize(): CommonPrize {
        if (!exists()) {
            return CommonPrize(descripcion = "Premio común", valor = 0)
        }
        val descripcion = getString("descripcion") ?: "Premio común"
        val valor = (getLong("valor") ?: 0L).toInt()
        return CommonPrize(descripcion = descripcion, valor = valor)
    }
}