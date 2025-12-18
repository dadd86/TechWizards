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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.ui.responsive.UiDims

@Composable
fun PantallaLobby(
    dims: UiDims,
    onVolver: () -> Unit
) {
    val scrollState = rememberScrollState()
    val lobbies = listOf(
        "#234" to stringResource(id = R.string.lobby_status_open),
        "#235" to stringResource(id = R.string.lobby_status_waiting),
        "#236" to stringResource(id = R.string.lobby_status_private)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dims.spaceMd, vertical = dims.spaceSm)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(dims.spaceSm)
    ) {
        TextButton(onClick = onVolver) {
            Text(text = stringResource(id = R.string.game_back_to_menu))
        }
        Text(
            text = stringResource(id = R.string.lobby_title),
            fontSize = dims.titleSp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(id = R.string.lobby_description),
            fontSize = dims.bodySp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column(verticalArrangement = Arrangement.spacedBy(dims.spaceXs)) {
            lobbies.forEach { (codigo, estado) ->
                LobbyCard(codigo = codigo, estado = estado, dims = dims)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dims.spaceSm)
        ) {
            Button(
                onClick = { /* TODO navegación crear lobby */ },
                modifier = Modifier
                    .weight(1f)
                    .height(dims.buttonHeightSm),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(id = R.string.lobby_create),
                    fontSize = dims.bodySp
                )
            }

            Button(
                onClick = { /* TODO navegación unir a lobby */ },
                modifier = Modifier
                    .weight(1f)
                    .height(dims.buttonHeightSm),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    text = stringResource(id = R.string.lobby_join),
                    fontSize = dims.bodySp
                )
            }
        }
    }
}

@Composable
private fun LobbyCard(
    codigo: String,
    estado: String,
    dims: UiDims
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dims.spaceSm, vertical = dims.spaceXs),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(dims.spaceXs)) {
                Text(
                    text = stringResource(id = R.string.lobby_code, codigo),
                    fontSize = dims.bodySp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = estado,
                    fontSize = dims.bodySp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { /* TODO join */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(id = R.string.lobby_action_join),
                    fontSize = dims.bodySp
                )
            }
        }
    }
}
