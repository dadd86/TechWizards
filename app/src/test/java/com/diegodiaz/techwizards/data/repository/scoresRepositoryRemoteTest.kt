package com.diegodiaz.techwizards.data.repository

import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.credenciales.EncryptedCredentialsStore
import com.diegodiaz.techwizards.data.infra.network.RetrofitProvider
import com.diegodiaz.techwizards.data.remote.api.ScoresApi
import com.diegodiaz.techwizards.data.remote.mapper.ScoreRemoteMapper
import com.diegodiaz.techwizards.data.repository.impl.ScoresRepositoryRemote
import com.diegodiaz.techwizards.domain.model.LeaderboardScore
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ScoresRepositoryRemoteTest {

    private val mockWebServer = MockWebServer()
    private val credentialsStore = EncryptedCredentialsStore()
    private lateinit var api: ScoresApi
    private lateinit var repository: ScoresRepositoryRemote

    @Before
    fun setUp() {
        credentialsStore.guardarFirebaseToken("firebase-token")
        mockWebServer.start()
        val retrofit = RetrofitProvider.retrofit(
            credentialsStore = credentialsStore,
            baseUrl = mockWebServer.url("/").toString(),
            serializer = "moshi",
            enableLogging = false,
        )
        api = retrofit.create(ScoresApi::class.java)
        repository = ScoresRepositoryRemote(
            scoresApi = api,
            mapper = ScoreRemoteMapper(),
            credentialsStore = credentialsStore,
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getTopTen adjunta token y mapea la respuesta`() = runBlocking {
        val body = """
            [
              {
                "id": "1",
                "player": "Ada",
                "points": 9000,
                "position": 1,
                "prize": {"name": "Poción", "description": "Rara"}
              }
            ]
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(body))

        val result = repository.obtenerTopTen()
        val request = mockWebServer.takeRequest()

        assertEquals("Bearer firebase-token", request.getHeader("Authorization"))
        assertEquals("firebase-token", request.requestUrl?.queryParameter("auth"))
        assertTrue(result is Result.Ok)
        val lista = (result as Result.Ok).value
        assertEquals(1, lista.size)
        assertEquals("Ada", lista.first().jugadorAlias)
        assertEquals(9000, lista.first().puntos)
        assertEquals("Poción", lista.first().premio?.nombre)
    }

    @Test
    fun `postScore envía el cuerpo correcto y devuelve el id remoto`() = runBlocking {
        val responseBody = """
            {
              "id": "99",
              "player": "Merlin",
              "points": 777,
              "position": 2
            }
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setResponseCode(201).setBody(responseBody))

        val input = LeaderboardScore(id = null, jugadorAlias = "Merlin", puntos = 777, posicion = null, premio = null)

        val result = repository.publicarScore(input)
        val request = mockWebServer.takeRequest()

        assertEquals("Bearer firebase-token", request.getHeader("Authorization"))
        assertTrue(request.body.readUtf8().contains("\"player\":\"Merlin\""))
        assertTrue(result is Result.Ok)
        assertEquals("99", (result as Result.Ok).value.id)
    }
}