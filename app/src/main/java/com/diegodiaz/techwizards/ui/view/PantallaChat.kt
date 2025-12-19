package com.diegodiaz.techwizards.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.ui.responsive.UiDims

@Composable
fun PantallaChat(
    dims: UiDims,
    onVolver: () -> Unit
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
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dims.spaceMd, vertical = dims.spaceSm),
        verticalArrangement = Arrangement.spacedBy(dims.spaceSm)
    ) {
        TextButton(onClick = onVolver) {
        Text(text = stringResource(id = R.string.game_back_to_menu))
    }
        Text(
            text = stringResource(id = R.string.chat_title),
            fontSize = dims.titleSp,
            fontWeight = FontWeight.Bold
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState),
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
