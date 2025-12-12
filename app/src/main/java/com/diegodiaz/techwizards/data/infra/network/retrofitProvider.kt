package com.diegodiaz.techwizards.data.infra.network

import okhttp3.HttpUrl
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
 *   - Query ?auth=<tokenFirebase>
 *
 * El token se obtiene mediante una lambda para no acoplarse a Firebase directamente.
 */
object RetrofitProvider {

    /**
     * Interceptor que, si hay token, lo añade como Bearer y como query `auth`.
     */
    class FirebaseAuthInterceptor(
        private val tokenProvider: () -> String?
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val originalRequest: Request = chain.request()
            val token = tokenProvider()

            if (token.isNullOrBlank()) {
                // Sin token => petición tal cual
                return chain.proceed(originalRequest)
            }

            // Añadir header Authorization y query auth
            val newUrl: HttpUrl = originalRequest.url.newBuilder()
                .setQueryParameter("auth", token)
                .build()

            val newRequest: Request = originalRequest.newBuilder()
                .url(newUrl)
                .header("Authorization", "Bearer $token")
                .build()

            return chain.proceed(newRequest)
        }
    }

    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    /**
     * Crea un OkHttpClient con logging y autenticación opcional.
     *
     * @param tokenProvider función que devuelve el token Firebase actual o null.
     */
    fun createOkHttpClient(
        tokenProvider: () -> String?
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(createLoggingInterceptor())
            .addInterceptor(FirebaseAuthInterceptor(tokenProvider))
            .build()
    }


    fun createRetrofit(
        baseUrl: String,
        tokenProvider: () -> String?
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createOkHttpClient(tokenProvider))
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
}
