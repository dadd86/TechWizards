package com.diegodiaz.techwizards.data.remote.prize

import com.diegodiaz.techwizards.domain.model.CommonPrize
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Fuente Firestore para administrar el premio común global.
 *
 * @security
 * - Opera únicamente sobre el documento singleton `/prize/common`.
 * - Evita registrar identificadores sensibles y depende de reglas de Firestore.
 */
class PremioComunFirestoreDataSource(
    private val firestore: FirebaseFirestore = Firebase.firestore,
) {

    private companion object {
        private const val DEFAULT_DESCRIPTION = "Premio común"
        private const val LEGACY_DESCRIPTION_FIELD = "description"
        private const val LEGACY_VALUE_FIELD = "value"
    }

    private val premioDocument = firestore.collection("prize").document("common")
    private val playersCollection = firestore.collection("players")

    /**
     * Obtiene el premio común actual.
     *
     * @return Premio común con descripción y valor.
     * @security Solo realiza lectura del documento público del premio.
     */
    suspend fun obtenerPremioComun(): CommonPrize {
        val snapshot = premioDocument.get().await()
        return snapshot.toCommonPrize()
    }

    /**
     * Observa el premio común en tiempo real.
     *
     * @return Flujo con el premio común actualizado.
     * @security Solo escucha el documento público del premio; no expone datos sensibles.
     */
    fun observarPremioComun(): Flow<CommonPrize> = callbackFlow {
        val listener = premioDocument.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val premio = snapshot?.toCommonPrize() ?: CommonPrize(
                descripcion = DEFAULT_DESCRIPTION,
                valor = 0,
                updatedAt = null
            )
            trySend(premio).isSuccess
        }
        awaitClose { listener.remove() }
    }

    /**
     * Actualiza el premio común (descripción y valor).
     *
     * @param nuevoPremio Datos validados del premio común.
     * @return Premio común persistido en Firestore.
     * @security Requiere reglas que permitan escritura autenticada.
     */
    suspend fun actualizarPremioComun(nuevoPremio: CommonPrize): CommonPrize {
        require(nuevoPremio.descripcion.isNotBlank()) { "descripcion vacía" }
        require(nuevoPremio.valor >= 0) { "valor negativo" }

        premioDocument.set(
            mapOf(
                "descripcion" to nuevoPremio.descripcion.trim(),
                "valor" to nuevoPremio.valor,
                "updatedAt" to FieldValue.serverTimestamp(),
                LEGACY_DESCRIPTION_FIELD to FieldValue.delete(),
                LEGACY_VALUE_FIELD to FieldValue.delete(),
            ),
            SetOptions.merge()
        ).await()

        return obtenerPremioComun()
    }

    /**
     * Incrementa el premio común por una derrota.
     *
     * @param delta Incremento positivo.
     * @return Premio común con el valor actualizado.
     * @security Operación transaccional y atómica sobre el documento.
     */
    suspend fun incrementarPremioComun(delta: Int): CommonPrize {
        require(delta > 0) { "delta debe ser > 0" }
        return firestore.runTransaction { tx ->
            val snapshot = tx.get(premioDocument)
            val descripcion = snapshot.descripcionNormalizada()
            val actual = snapshot.valorNormalizado()
            val nuevoValor = actual + delta

            tx.set(
                premioDocument,
                mapOf(
                    "descripcion" to descripcion,
                    "valor" to nuevoValor,
                    "updatedAt" to FieldValue.serverTimestamp(),
                    LEGACY_DESCRIPTION_FIELD to FieldValue.delete(),
                    LEGACY_VALUE_FIELD to FieldValue.delete(),
                ),
                SetOptions.merge()
            )
            CommonPrize(descripcion = descripcion, valor = nuevoValor.toInt())
        }.await()
    }

    /**
     * Reclama el premio común y lo resetea a cero.
     *
     * @param firebaseUid UID autenticado del jugador.
     * @param alias Alias visible para registrar el cobro.
     * @param claimId Identificador de reclamo (auditoría).
     * @return Valor del premio común cobrado.
     * @security
     * - Transacción atómica para evitar cobros concurrentes.
     * - Solo actualiza el documento del jugador autenticado.
     */
    suspend fun reclamarPremioComun(
        firebaseUid: String,
        alias: String,
        claimId: String,
    ): Int {
        require(firebaseUid.isNotBlank()) { "firebaseUid vacío" }
        require(alias.isNotBlank()) { "alias vacío" }
        require(claimId.isNotBlank()) { "claimId vacío" }

        return firestore.runTransaction { tx ->
            val snapshot = tx.get(premioDocument)
            val descripcion = snapshot.descripcionNormalizada()
            val premioComun = snapshot.valorNormalizado()
            if (premioComun <= 0L) return@runTransaction 0

            tx.set(
                premioDocument,
                mapOf(
                    "descripcion" to descripcion,
                    "valor" to 0,
                    "updatedAt" to FieldValue.serverTimestamp(),
                    "lastWinnerUid" to firebaseUid,
                    "lastWinnerAlias" to alias.trim(),
                    "lastClaimId" to claimId,
                    "lastClaimAt" to FieldValue.serverTimestamp(),
                    LEGACY_DESCRIPTION_FIELD to FieldValue.delete(),
                    LEGACY_VALUE_FIELD to FieldValue.delete(),
                ),
                SetOptions.merge()
            )

            val playerDocument = playersCollection.document(firebaseUid)
            tx.set(
                playerDocument,
                mapOf(
                    "alias" to alias.trim(),
                    "coins" to FieldValue.increment(premioComun),
                    "lastPrizeClaimId" to claimId,
                    "lastPrizeClaimedAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp(),
                ),
                SetOptions.merge()
            )

            premioComun.toInt()
        }.await()
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toCommonPrize(): CommonPrize {
        val descripcion = descripcionNormalizada()
        val valor = valorNormalizado().toInt()
        val updatedAt = getTimestamp("updatedAt")?.toDate()?.time
        return CommonPrize(descripcion = descripcion, valor = valor, updatedAt = updatedAt)
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.descripcionNormalizada(): String {
        val descripcionActual = getString("descripcion")
        val descripcionLegacy = getString(LEGACY_DESCRIPTION_FIELD)
        return listOf(descripcionActual, descripcionLegacy)
            .firstOrNull { !it.isNullOrBlank() }
            ?.trim()
            ?: DEFAULT_DESCRIPTION
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.valorNormalizado(): Long {
        return getLong("valor")
            ?: getLong(LEGACY_VALUE_FIELD)
            ?: 0L
    }
}