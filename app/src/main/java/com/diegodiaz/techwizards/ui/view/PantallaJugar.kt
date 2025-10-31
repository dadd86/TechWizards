package com.diegodiaz.techwizards.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.diegodiaz.techwizards.ui.viewmodel.JuegoViewModel
import com.diegodiaz.techwizards.domain.model.Resultado

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaJugar(
    isDarkTheme: Boolean,
    onVolverAlMenu: () -> Unit
) {
    val viewModel: JuegoViewModel = viewModel()
    val monedas by viewModel.monedas.collectAsState()
    val ultimoResultado by viewModel.ultimoResultado.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("🎲 Jugar") },
                navigationIcon = {
                    IconButton(onClick = onVolverAlMenu) {
                        Text("⬅️")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mostrar monedas actuales
            Text(
                text = "Monedas: $monedas",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón principal para lanzar el dado
            Button(onClick = { viewModel.lanzarDado() }) {
                Text("🎲 Lanzar dado")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mostrar el último resultado
            Text(
                text = when (ultimoResultado) {
                    null -> "Aún no has jugado"
                    Resultado.VICTORIA -> "¡Ganaste! 🎉"
                    Resultado.DERROTA -> "Perdiste 😢"
                },
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
