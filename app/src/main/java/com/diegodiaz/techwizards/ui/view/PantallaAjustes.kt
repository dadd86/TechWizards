// ui/view/PantallaAjustes.kt
package com.diegodiaz.techwizards.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PantallaAjustes(
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    onVolverAlMenu: () -> Unit
) {
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
            Text("Ajustes", style = MaterialTheme.typography.titleLarge)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tema oscuro")
                Switch(checked = isDarkTheme, onCheckedChange = onToggleTheme)
            }
        }
        Button(
            onClick = onVolverAlMenu,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.6f)
                .padding(bottom = 28.dp)
        ) { Text("Volver al menú") }
    }
}
