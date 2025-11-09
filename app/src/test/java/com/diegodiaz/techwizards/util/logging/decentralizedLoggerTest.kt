package com.diegodiaz.techwizards.util.logging

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

private class TestSink : LogSink {
    val count = AtomicInteger(0)
    var lastMessage: String? = null
    override fun log(level: String, tag: String, message: String, throwable: Throwable?) {
        count.incrementAndGet()
        lastMessage = message
    }
}

class DecentralizedLoggerTest {

    private lateinit var sink: TestSink

    @Before
    fun setup() {
        sink = TestSink()
        // Nota: en un test real, preferirías un método para limpiar sinks registrados.
        // Para simplificar: registramos una sola clase testSink que no se duplica por tipo.
        DecentralizedLogger.registerSink(sink)
        DecentralizedLogger.setMinLevel(LogLevel.DEBUG)
    }

    @Test
    fun deberiaEmitirMensaje_cuandoNivelEsSuficiente() {
        DecentralizedLogger.d("Test", "hola")
        assertEquals(1, sink.count.get())
        assertEquals("hola", sink.lastMessage)
    }

    @Test
    fun deberiaEnmascararPii_cuandoCoincideRegex() {
        DecentralizedLogger.addPiiMask(Regex("[0-9]{16}")) // tarjeta simple (demo)
        DecentralizedLogger.i("Test", "card=1234567812345678")
        assertEquals("card=***", sink.lastMessage)
    }
}
