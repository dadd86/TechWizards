package com.diegodiaz.techwizards.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.diegodiaz.techwizards.domain.model.Lobby
import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.ui.controller.LobbyUiState
import com.diegodiaz.techwizards.ui.controller.MatchOnlineUiState
import com.diegodiaz.techwizards.ui.responsive.UiDims

@Composable
fun PantallaLobby(
    dims: UiDims,
    lobbyState: LobbyUiState,
    matchState: MatchOnlineUiState,
    onVolver: () -> Unit,
    onCrearLobby: () -> Unit,
    onActualizarCodigo: (String) -> Unit,
    onUnirsePorCodigo: () -> Unit,
    onEntrarLobby: (String) -> Unit,
    onSeleccionCara: (Int) -> Unit,
    onConfirmarApuesta: () -> Unit,
    onLanzarDado: () -> Unit,
    onBuscarRival: () -> Unit
) {
    val scrollState = rememberScrollState()

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

        OutlinedTextField(
            value = lobbyState.codigoIngreso,
            onValueChange = onActualizarCodigo,
            label = { Text(stringResource(id = R.string.lobby_join_placeholder)) },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dims.spaceSm)
        ) {
            Button(
                onClick = onCrearLobby,
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
                onClick = onUnirsePorCodigo,
                modifier = Modifier
                    .weight(1f)
                    .height(dims.buttonHeightSm),
                enabled = lobbyState.codigoIngreso.isNotBlank(),
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
        LobbyListado(lobbies = lobbyState.lobbies, dims = dims, onEntrarLobby = onEntrarLobby)

        if (matchState.matchId != null) {
            HorizontalDivider()
            Text(
                text = stringResource(id = R.string.lobby_active_match, matchState.lobbyId ?: matchState.matchId),
                fontSize = dims.bodySp,
                fontWeight = FontWeight.SemiBold
            )
            PantallaMatch(
                dims = dims,
                uiState = matchState,
                onSeleccionCara = onSeleccionCara,
                onConfirmarApuesta = onConfirmarApuesta,
                onLanzarDado = onLanzarDado,
                onBuscarRival = onBuscarRival,
                onVolver = onVolver,
                enableScroll = false
            )
        } else {
            Spacer(modifier = Modifier.height(dims.spaceSm))
            Text(
                text = stringResource(id = R.string.lobby_waiting_to_start),
                fontSize = dims.bodySp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LobbyListado(
    lobbies: List<Lobby>,
    dims: UiDims,
    onEntrarLobby: (String) -> Unit
) {
    if (lobbies.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = stringResource(id = R.string.lobby_empty),
                modifier = Modifier.padding(all = dims.spaceSm),
                fontSize = dims.bodySp
            )
        }
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(dims.spaceXs)) {
        lobbies.forEach { lobby ->
            LobbyCard(lobby = lobby, dims = dims, onEntrarLobby = onEntrarLobby)
        }
    }
}

@Composable
private fun LobbyCard(
    lobby: Lobby,
    dims: UiDims,
    onEntrarLobby: (String) -> Unit
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
                    text = stringResource(id = R.string.lobby_code, lobby.codigo ?: lobby.id),
                    fontSize = dims.bodySp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = lobby.modo,
                    fontSize = dims.bodySp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { onEntrarLobby(lobby.id) },
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
