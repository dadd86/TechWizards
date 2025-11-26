package com.diegodiaz.techwizards.util.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

/**
 * Proveedor ligero para capturar la ubicación del jugador al ganar.
 *
 * @param appContext Contexto de aplicación para obtener el `LocationManager`.
 * @param ioDispatcher Dispatcher para aislar trabajo bloqueante.
 * @security Solo se exponen coordenadas sin identificadores personales.
 */
class LocationTracker(
    private val appContext: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    /**
     * Recupera la última ubicación conocida o solicita una lectura puntual.
     *
     * @return [LocationSnapshot] con coordenadas o `null` si no está disponible.
     * @throws SecurityException Si faltan permisos `ACCESS_FINE_LOCATION` o `ACCESS_COARSE_LOCATION`.
     */
    suspend fun captureCurrentLocation(): LocationSnapshot? = withContext(ioDispatcher) {
        if (!hasLocationPermission()) {
            DecentralizedLogger.w(TAG, "Sin permisos de localización; no se capturará posición")
            return@withContext null
        }

        val manager = appContext.getSystemService(LocationManager::class.java)
            ?: return@withContext null

        val provider = when {
            manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ->
                LocationManager.GPS_PROVIDER
            manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ->
                LocationManager.NETWORK_PROVIDER
            else -> null
        } ?: return@withContext null

        // ✅ usar getLastKnownLocation en lugar de una propiedad inexistente
        manager.getLastKnownLocation(provider)?.let { lastKnown ->
            return@withContext lastKnown.toSnapshot()
        }

        // Si no hay última conocida, pedimos una lectura puntual con timeout
        withTimeoutOrNull(5_000L) {
            suspendCancellableCoroutine<LocationSnapshot?> { continuation ->
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        DecentralizedLogger.d(TAG, "Lectura puntual recibida del proveedor=$provider")
                        val snapshot = location.toSnapshot()
                        continuation.resume(snapshot)
                        manager.removeUpdates(this)
                    }

                    override fun onProviderDisabled(provider: String) {
                        DecentralizedLogger.w(TAG, "Proveedor deshabilitado durante captura: $provider")
                    }
                }

                try {
                    @Suppress("MissingPermission")
                    manager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
                } catch (e: SecurityException) {
                    // Por si acaso los permisos cambian en tiempo de ejecución
                    DecentralizedLogger.e(TAG, "Permiso de localización revocado en requestSingleUpdate", e)
                    continuation.resume(null)
                    manager.removeUpdates(listener)
                }

                continuation.invokeOnCancellation {
                    manager.removeUpdates(listener)
                }
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineGranted || coarseGranted
    }

    private fun Location.toSnapshot(): LocationSnapshot = LocationSnapshot(
        latitude = latitude,
        longitude = longitude,
        accuracyMetres = if (hasAccuracy()) accuracy else null,
        timestampMillis = time
    )

    companion object {
        private const val TAG = "LocationTracker"
    }
}

/**
 * Valor inmutable con los datos mínimos de ubicación para auditoría de victorias.
 *
 * @security No contiene identificadores de jugador.
 */
data class LocationSnapshot(
    val latitude: Double,
    val longitude: Double,
    val accuracyMetres: Float?,
    val timestampMillis: Long
)
