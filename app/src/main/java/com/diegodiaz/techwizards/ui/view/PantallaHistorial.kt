package com.diegodiaz.techwizards.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(Modifier.height(40.dp))
            Text(
                text = "Historial de partidas",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(40.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .background(Color.White, shape = RoundedCornerShape(36.dp))
                    .padding(vertical = 24.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HistorialItem("02/10/2025", "Ganó", "+50") //CAMBIAR!!! SON VARIABLES
                    HistorialItem("02/10/2025", "Perdió", "-30")
                    HistorialItem("02/10/2025", "Ganó", "+50")
                    HistorialItem("02/10/2025", "Perdió", "-30")
                }
            }
        }
        Button(
            onClick = onVolverAlMenu,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.6f)
                .padding(bottom = 24.dp)
                .clip(RoundedCornerShape(20.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text(
                text = "Volver al menú",
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun HistorialItem(
    fecha: String,
    resultado: String,
    monedas: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .background(Color(0xFFF3F3F3), shape = RoundedCornerShape(20.dp))
            .padding(vertical = 18.dp, horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = fecha,
            fontSize = 20.sp,
            color = Color(0xFF555555),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = resultado,
            fontSize = 20.sp,
            color = Color(0xFF222222),
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = monedas,
            fontSize = 20.sp,
            color = Color(0xFF555555),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}
