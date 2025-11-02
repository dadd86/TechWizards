package com.diegodiaz.techwizards.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.diegodiaz.techwizards.ui.responsive.Responsive
import com.diegodiaz.techwizards.ui.responsive.UiDims


@Composable
fun PantallaAjustes(
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    onVolverAlMenu: () -> Unit
) = Responsive { dims ->

    val fondo = if (!isDarkTheme) Color(0xFFB5E2F8) else MaterialTheme.colorScheme.background

    Scaffold(
        containerColor = fondo,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dims.spaceSm),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onVolverAlMenu,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(dims.buttonHeightSm)
                        .clip(RoundedCornerShape(20.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(
                        text = "Volver al menú",
                        color = Color(0xFF3B71B8),
                        fontWeight = FontWeight.Bold,
                        fontSize = dims.bodySp
                    )
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dims.spaceMd)
                .verticalScroll(rememberScrollState()), // 👈 Scroll agregado
            verticalArrangement = Arrangement.spacedBy(dims.spaceSm)
        ) {
            AjustesSeccion("Sonido", dims) {
                AjusteSwitch("Música de fondo", true)
                AjusteSwitch("Efectos de sonido", false)
            }

            AjustesSeccion("Visualización", dims) {
                AjusteSwitch("Tema claro/oscuro", isDarkTheme, onToggleTheme)
                AjusteSwitch("Animaciones gráficas del juego", false)
            }

            AjustesSeccion("Notificaciones", dims) {
                AjusteSwitch("Notificaciones de resultado o logros", true)
            }

            Spacer(Modifier.height(dims.spaceLg))
        }
    }
}

@Composable
private fun AjustesSeccion(
    titulo: String,
    dims: UiDims,
    content: @Composable ColumnScope.() -> Unit
) {
    Text(
        text = titulo,
        fontSize = dims.titleSp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
    Column(verticalArrangement = Arrangement.spacedBy(dims.spaceXs), content = content)
}

@Composable
private fun AjusteSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
