package com.diegodiaz.techwizards.data.infra.network

import com.diegodiaz.techwizards.BuildConfig
import com.diegodiaz.techwizards.credenciales.CredentialsStore
import com.diegodiaz.techwizards.core.SessionManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Fabrica clientes OkHttp y Retrofit con logging y autenticación opcional.
 *
 * - Usa un interceptor que añade:
 *   - Cabecera Authorization: Bearer <tokenFirebase>
 *
 * El token se obtiene mediante una lambda para no acoplarse a Firebase directamente.
 */
object RetrofitProvider {

    // ✅ Moshi configurado para Kotlin (esto arregla tu crash del ranking)
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    /**
     * Interceptor que, si hay token, lo añade como cabecera Bearer.
     */
    class FirebaseAuthInterceptor(
        private val tokenProvider: () -> String?,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val originalRequest: Request = chain.request()

            // ✅ Si ya trae Authorization (por @Header en Retrofit), no lo toques
            if (originalRequest.header("Authorization") != null) {
                return chain.proceed(originalRequest)
            }

            val token = tokenProvider()
            if (token.isNullOrBlank()) return chain.proceed(originalRequest)

            val newRequest: Request = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()

            return chain.proceed(newRequest)
        }
    }

    /**
     * Interceptor que añade la cabecera Authorization leyendo desde [SessionManager].
     *
     * @param sessionManager Fuente in-memory de la sesión autenticada.
     * @security No expone el token en logs y opera únicamente en memoria.
     */
    class SessionAuthInterceptor(
        private val sessionManager: SessionManager,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val original = chain.request()

            // ✅ Si ya trae Authorization (por @Header en Retrofit), no lo toques
            if (original.header("Authorization") != null) {
                return chain.proceed(original)
            }

            val token = sessionManager.session.value?.token
            if (token.isNullOrBlank()) return chain.proceed(original)

            val newRequest = original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()

            return chain.proceed(newRequest)
        }
    }


    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
            redactHeader("Authorization")
        }
    }

    /**
     * Crea un OkHttpClient con logging y autenticación opcional.
     *
     * @param tokenProvider función que devuelve el token Firebase actual o null.
     */
    fun createOkHttpClient(
        tokenProvider: () -> String?,
        sessionManager: SessionManager? = null,
        enableLogging: Boolean = true,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(FirebaseAuthInterceptor(tokenProvider))

        sessionManager?.let { manager ->
            builder.addInterceptor(SessionAuthInterceptor(manager))
        }

        if (enableLogging) {
            builder.addInterceptor(createLoggingInterceptor())
        }

        return builder.build()
    }

    fun createRetrofit(
        baseUrl: String,
        tokenProvider: () -> String?,
        sessionManager: SessionManager? = null,
        enableLogging: Boolean = true,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createOkHttpClient(tokenProvider, sessionManager, enableLogging))
            .addConverterFactory(MoshiConverterFactory.create(moshi)) // ✅ aquí
            .build()
    }

    /**
     * Atajo para construir Retrofit leyendo token desde un [CredentialsStore].
     */
    fun retrofit(
        credentialsStore: CredentialsStore,
        sessionManager: SessionManager? = null,
        baseUrl: String = BuildConfig.API_BASE_URL,
        serializer: String = BuildConfig.API_SERIALIZER,
        enableLogging: Boolean = true,
    ): Retrofit {
        val tokenProvider = { credentialsStore.obtenerFirebaseToken() }

        val converter = when (serializer.lowercase()) {
            "gson" -> retrofit2.converter.gson.GsonConverterFactory.create()
            else -> MoshiConverterFactory.create(moshi) // ✅ y aquí
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createOkHttpClient(tokenProvider, sessionManager, enableLogging))
            .addConverterFactory(converter)
            .build()
    }
}
