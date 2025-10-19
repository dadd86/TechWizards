package com.diegodiaz.techwizards.util.logging

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

private class testSink : LogSink {
    val count = AtomicInteger(0)
    var lastMessage: String? = null
    override fun emit(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        count.incrementAndGet()
        lastMessage = message
    }
}

class loggingDecentralizedLoggerTest {

    private lateinit var sink: testSink

    @Before
    fun setup() {
        sink = testSink()
        // Nota: en un test real, preferirías un método para limpiar sinks registrados.
        // Para simplificar: registramos una sola clase testSink que no se duplica por tipo.
        loggingDecentralizedLogger.registerSink(sink)
        loggingDecentralizedLogger.setMinLevel(LogLevel.DEBUG)
    }

    @Test
    fun deberiaEmitirMensaje_cuandoNivelEsSuficiente() {
        loggingDecentralizedLogger.d("Test", "hola")
        assertEquals(1, sink.count.get())
        assertEquals("hola", sink.lastMessage)
    }

    @Test
    fun deberiaEnmascararPii_cuandoCoincideRegex() {
        loggingDecentralizedLogger.addPiiMask(Regex("[0-9]{16}")) // tarjeta simple (demo)
        loggingDecentralizedLogger.i("Test", "card=1234567812345678")
        assertEquals("card=***", sink.lastMessage)
    }
}
