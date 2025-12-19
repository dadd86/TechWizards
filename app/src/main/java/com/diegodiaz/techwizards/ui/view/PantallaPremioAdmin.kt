package com.diegodiaz.techwizards.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.ui.controller.ControladorPremioAdmin
import com.diegodiaz.techwizards.ui.controller.PremioAdminUiState
import com.diegodiaz.techwizards.ui.responsive.UiDims

/**
 * Pantalla administrativa para consultar y actualizar el premio común.
 *
 * @security Solo muestra datos públicos del premio; la autorización se valida en capa de dominio y backend.
 */
@Composable
fun PantallaPremioAdmin(
    dims: UiDims,
    controlador: ControladorPremioAdmin,
    onVolver: () -> Unit,
) {
    val estado by controlador.ui.collectAsState()
    val descripcion = remember { mutableStateOf("") }
    val valor = remember { mutableStateOf("0") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dims.spaceMd),
        verticalArrangement = Arrangement.spacedBy(dims.spaceSm),
    ) {
        TextButton(onClick = onVolver) { Text(text = stringResource(id = R.string.game_back_to_menu)) }
        Text(
            text = "Premio común (admin)",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        when (val ui = estado) {
            PremioAdminUiState.Cargando -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(dims.spaceSm))
                    Text("Cargando premio...")
                }
            }

            is PremioAdminUiState.Error -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(dims.spaceXs)
                ) {
                    Text(ui.mensaje, color = MaterialTheme.colorScheme.error)
                    Button(onClick = controlador::cargarPremio) { Text("Reintentar") }
                }
            }

            is PremioAdminUiState.Exito -> {
                val premio = ui.premio

                LaunchedEffect(premio) {
                    descripcion.value = premio.descripcion
                    valor.value = premio.valor.toString()
                }

                ui.mensaje?.let { mensaje ->
                    val color = if (mensaje.esError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                    Text(text = mensaje.texto, color = color)
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(dims.spaceSm),
                        verticalArrangement = Arrangement.spacedBy(dims.spaceXs)
                    ) {
                        Text(text = "Descripción actual: ${premio.descripcion}")
                        Text(text = "Valor actual: ${premio.valor}")
                    }
                }

                OutlinedTextField(
                    value = descripcion.value,
                    onValueChange = { descripcion.value = it },
                    label = { Text("Nueva descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = valor.value,
                    onValueChange = { valor.value = it },
                    label = { Text("Nuevo valor") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        val nuevoValor = valor.value.toIntOrNull() ?: 0
                        controlador.actualizarPremio(descripcion.value, nuevoValor)
                    }
                ) {
                    Text("Guardar cambios")
                }
            }
        }
    }
}