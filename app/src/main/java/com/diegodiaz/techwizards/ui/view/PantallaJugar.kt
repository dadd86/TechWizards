package com.diegodiaz.techwizards.ui.view


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel
import com.diegodiaz.techwizards.core.ServiceLocator
import com.diegodiaz.techwizards.data.local.entity.Resultado
import com.diegodiaz.techwizards.ui.controller.ControladorJuego
import com.diegodiaz.techwizards.ui.controller.SimpleVmFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PantallaJugar(onBack: () -> Unit) {
    val viewModel: ControladorJuego = viewModel(
        factory = SimpleVmFactory {
            ControladorJuego(
                repo = ServiceLocator.juegoRepository,
                usuarioId = ControladorJuego.DEFAULT_USUARIO_ID
            )
        }
    )

    val uiState by viewModel.ui.collectAsState()
    val historial by viewModel.historial.collectAsState()
    val formatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        Text("🎲 Lanza el dado y prueba suerte", style = MaterialTheme.typography.headlineSmall)
        Text("Monedas disponibles: ${uiState.monedas}", style = MaterialTheme.typography.titleMedium)
        if (uiState.ultimoResultado.isNotBlank()) {
            Surface(shadowElevation = 2.dp) {
                Text(
                    text = uiState.ultimoResultado,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
        if (uiState.error != null) {
            Text(
                text = uiState.error ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Button(
            onClick = { viewModel.lanzar() },
            enabled = !uiState.cargando,
        ) {
            if (uiState.cargando) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp))
            } else {
                Text("🎯 Lanzar dado")
            }
        }
        Divider()
        Text(
            text = "Historial reciente",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(historial) { partida ->
                val fecha = formatter.format(Date(partida.fecha))
                val gano = partida.resultado == Resultado.GANADO
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = fecha, style = MaterialTheme.typography.bodySmall)
                        Text(
                            text = if (gano) "Ganaste" else "Perdiste",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Δ monedas: ${partida.deltaMonedas}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Volver al menú")
        }
    }
}