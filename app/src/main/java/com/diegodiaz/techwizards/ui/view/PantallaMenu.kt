package com.diegodiaz.techwizards.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * PantallaMenu.kt
 *
 * Menú principal de la aplicación.
 * Desde aquí el jugador puede elegir si quiere jugar, ver historial o cambiar ajustes.
 */
@Composable
fun PantallaMenu(
    onNavigateToJugar: () -> Unit,
    onNavigateToHistorial: () -> Unit,
    onNavigateToAjustes: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Menú Principal", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(40.dp))

        Button(onClick = onNavigateToJugar) { Text("🎯 Jugar") }
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = onNavigateToHistorial) { Text("📜 Historial") }
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = onNavigateToAjustes) { Text("⚙️ Ajustes") }
    }
}
