package com.diegodiaz.techwizards.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.diegodiaz.techwizards.core.ServiceLocator
import com.diegodiaz.techwizards.data.local.entity.Resultado
import com.diegodiaz.techwizards.ui.controller.ControladorHistorial
import com.diegodiaz.techwizards.ui.controller.ControladorJuego
import com.diegodiaz.techwizards.ui.controller.SimpleVmFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PantallaHistorial(
    isDarkTheme: Boolean,
    onVolverAlMenu: () -> Unit
) {
    val viewModel: ControladorHistorial = viewModel(
        factory = SimpleVmFactory {
            ControladorHistorial(
                repository = ServiceLocator.juegoRepository,
                usuarioId = ControladorJuego.DEFAULT_USUARIO_ID,
            )
        }
    )
    val historial by viewModel.historial.collectAsState()
    val formatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (!isDarkTheme) Color(0xFFF9F9F9) else MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Spacer(Modifier.height(40.dp))
            Text("Historial de partidas", fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(24.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 3.dp
            ) {
                if (historial.isEmpty()) {
                    Text(
                        text = "Aún no hay partidas registradas",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(historial) { partida ->
                            val fecha = formatter.format(Date(partida.fecha))
                            HistorialItem(
                                fecha = fecha,
                                resultado = if (partida.resultado == Resultado.GANADO) "Ganó" else "Perdió",
                                monedas = partida.deltaMonedas
                            )
                        }
                    }
                }
            }
        }
        Button(
            onClick = onVolverAlMenu,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.6f)
                .padding(bottom = 24.dp)
        ) { Text("Volver al menú") }
    }
}

@Composable

private fun HistorialItem(fecha: String, resultado: String, monedas: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .background(Color(0xFFF3F3F3), shape = RoundedCornerShape(20.dp))
            .padding(vertical = 18.dp, horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(fecha, fontSize = 18.sp, color = Color(0xFF555555), modifier = Modifier.weight(2f))
        Text(resultado, fontSize = 20.sp, color = Color(0xFF222222), fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(2f))
        Text(
            if (monedas >= 0) "+$monedas" else monedas.toString(),
            fontSize = 18.sp,
            color = if (monedas >= 0) Color(0xFF2E7D32) else Color(0xFFC62828),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}