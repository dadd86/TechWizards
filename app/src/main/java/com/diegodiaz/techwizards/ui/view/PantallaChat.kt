package com.diegodiaz.techwizards.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.ui.responsive.UiDims

@Composable
fun PantallaChat(
    dims: UiDims
) {
    val welcome = stringResource(id = R.string.chat_system_welcome)
    val hint = stringResource(id = R.string.chat_hint_controls)

    val mensajes = remember(welcome, hint) {
        mutableStateListOf(
            welcome,
            hint
        )
    }

    var input by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dims.spaceMd, vertical = dims.spaceSm),
        verticalArrangement = Arrangement.spacedBy(dims.spaceSm)
    ) {
        Text(
            text = stringResource(id = R.string.chat_title),
            fontSize = dims.titleSp,
            fontWeight = FontWeight.Bold
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(dims.spaceXs)
        ) {
            mensajes.forEach { mensaje ->
                ChatBubble(texto = mensaje, dims = dims)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dims.spaceSm)
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(id = R.string.chat_placeholder)) }
            )

            Button(
                onClick = {
                    if (input.isNotBlank()) {
                        mensajes.add(input.trim())
                        input = ""
                    }
                },
                modifier = Modifier.height(dims.buttonHeightSm),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(id = R.string.chat_send),
                    fontSize = dims.bodySp
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(texto: String, dims: UiDims) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(
                horizontal = dims.spaceSm,
                vertical = dims.spaceXs
            ),
            fontSize = dims.bodySp
        )
    }
}
