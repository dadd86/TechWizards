// ui/view/PantallaHistorial.kt
package com.diegodiaz.techwizards.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement


@Composable
fun PantallaHistorial(
    isDarkTheme: Boolean,
    onVolverAlMenu: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (!isDarkTheme) Color(0xFFF9F9F9) else MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Spacer(Modifier.height(40.dp))
            Text("Historial de partidas", fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(40.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .background(Color.White, shape = RoundedCornerShape(36.dp))
                    .padding(vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                HistorialItem("02/10/2025", "Ganó", "+50")
                HistorialItem("02/10/2025", "Perdió", "-30")
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
private fun HistorialItem(fecha: String, resultado: String, monedas: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .background(Color(0xFFF3F3F3), shape = RoundedCornerShape(20.dp))
            .padding(vertical = 18.dp, horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(fecha, fontSize = 20.sp, color = Color(0xFF555555), modifier = Modifier.weight(2f))
        Text(resultado, fontSize = 20.sp, color = Color(0xFF222222), fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(2f))
        Text(monedas, fontSize = 20.sp, color = Color(0xFF555555), modifier = Modifier.weight(1f), textAlign = TextAlign.End)
    }
}
