package com.diegodiaz.techwizards.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * PantallaBienvenida.kt
 *
 * Pantalla que aparece al iniciar la app.
 * Muestra un mensaje y un botón para pasar al menú principal.
 */
@Composable
fun PantallaBienvenida(onNavigateToMenu: () -> Unit) {
    // Centra todo el contenido en pantalla.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎲 ¡Bienvenido a Tech Wizards!",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onNavigateToMenu) {
                Text("Comenzar")
            }
        }
    }
}
