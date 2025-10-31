/*package com.diegodiaz.techwizards.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

/**
 * PantallaHistorial.kt
 *
 * Muestra partidas anteriores (por ahora, simuladas).
 */
@Composable
fun PantallaHistorial(onBack: () -> Unit) {

    val partidasEjemplo = listOf(
        PartidaFake("Partida 1", true, 10, Date()),
        PartidaFake("Partida 2", false, -5, Date()),
        PartidaFake("Partida 3", true, 10, Date())
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📜 Historial de Partidas") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("⬅️") }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            items(partidasEjemplo) { partida ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(partida.nombre, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Resultado: ${if (partida.ganada) "Ganada" else "Perdida"}")
                        Text("Monedas: ${partida.monedas}")
                        Text(
                            "Fecha: ${
                                SimpleDateFormat("dd/MM/yyyy HH:mm").format(partida.fecha)
                            }",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

data class PartidaFake(
    val nombre: String,
    val ganada: Boolean,
    val monedas: Int,
    val fecha: Date
)
*/