# `domain.model`

Modelos de datos inmutables que representan el estado del juego tanto online como offline. Se diseñan para ser serializables y fáciles de mapear a entidades Room.

## Categorías

### Jugadores y economía
- `Usuario`: Perfil local del jugador (alias, moneda, UID remoto opcional).
- `Monedero`: Saldo actual y referencia al usuario.
- `Partida`: Resultado de un lanzamiento de dado con delta de monedas.

### Configuración
- `GameSettings`: Preferencias de audio, animaciones, tema, idioma y notificaciones.

### Multiplayer
- `Lobby`: Sala previa que agrupa jugadores antes de iniciar un match.
- `Match`: Partida multijugador con estado (`CREATED`, `STARTED`, `FINISHED`).
- `MatchParticipant`: Asociación jugador↔match y su rol.
- `MatchScore`: Resultado final por participante.
- `MatchEvent`: Evento cronológico (ej. acción, turno) almacenado para replay.

### Mensajería y eventos
- `Message`: Mensajes de chat en salas/matches.
- `Evento`: Retos temporales o campañas internas.

### Sincronización offline-first
- `Outbox`: Cola de operaciones pendientes a sincronizar con el backend.
- `Tombstone`: Marca de borrado para replicación eventual.
- `IdMap`: Relación entre IDs temporales (cliente) y definitivos (servidor).

### Celebraciones
- `VictoryLocation`: Datos asociados a celebraciones de victoria (geolocalización u otra referencia).

Todos los modelos son `data class` y preservan valores primitivos o enums para facilitar su serialización en Room/JSON.