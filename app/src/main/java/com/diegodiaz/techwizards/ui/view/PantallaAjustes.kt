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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.ui.responsive.Responsive

@Composable
fun PantallaAjustes(
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    onVolverAlMenu: () -> Unit
)= Responsive { dims ->
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (!isDarkTheme) Color(0xFFB5E2F8) else MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            AjustesSeccion(titulo = "Sonido") {
                AjusteSwitch("Música de fondo", checked = true)
                AjusteSwitch("Efectos de sonido", checked = false)
            }
            AjustesSeccion(titulo = "Visualización") {
                AjusteSwitch(
                    label = "Tema claro/oscuro",
                    checked = isDarkTheme,
                    onCheckedChange = onToggleTheme
                )
                AjusteSwitch("Animaciones gráficas del juego", checked = false)
            }
            AjustesSeccion(titulo = "Notificaciones") {
                AjusteSwitch("Notificaciones de resultado o logros", checked = true)
            }
            Text(
                text = "Monedas / Datos de jugador",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.moneda),
                    contentDescription = "Moneda",
                    tint = Color(0xFFFFC947),
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = " Monedas: 100",
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = { /* Acción: reiniciar monedas */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Reiniciar monedas", fontWeight = FontWeight.Bold)
            }
            Text(
                text = "Información",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { /* Acción versión de la app */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Versión de la app", color = Color.Black)
                }
                Button(
                    onClick = { /* Acción acerca de */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Acerca de", color = Color.Black)
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
        // Botón de volver al menú
        Button(
            onClick = onVolverAlMenu,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.6f)
                .padding(bottom = 28.dp)
                .clip(RoundedCornerShape(20.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text(
                text = "Volver al menú",
                color = Color(0xFF3B71B8),
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
        }
    }
}

// HELPER COMPONENTES
@Composable
fun AjustesSeccion(
    titulo: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Text(
        text = titulo,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
}

@Composable
fun AjusteSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 16.sp, color = Color(0xFF3A3A3A))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
