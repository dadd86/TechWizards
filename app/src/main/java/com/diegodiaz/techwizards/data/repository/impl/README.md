# `data.repository.impl`

Implementaciones concretas de repositorios usando Room y DataStore.

## Repositorios y dependencias

| Clase | Dependencias | Funciones clave |
| --- | --- | --- |
| `JuegoRepositoryRoom` | `IUsuarioDao`, `IMonederoDao`, `IPartidaDao`. | Flujo de saldo (`observarSaldo`, `observeSaldoRx`), historial (`observarHistorial`), inicialización (`inicializarMonedas`, `inicializarMonedasRx`) y registrar tiradas (`lanzarDado`). |
| `MatchRepositoryRoom` | `IPartidaDao`, `IMonederoDao`, `IMatchDao`, `IMatchEventDao`, `IMatchParticipantDao`, `IMatchScoreDao`, `TransactionRunner`. | Crear/actualizar matches, registrar eventos, guardar puntuaciones y sincronizar saldos tras partidas multijugador. |
| `LobbyRepositoryRoom` | `ILobbyDao`, `IUsuarioDao`, `IMatchDao`. | Mantener salas disponibles, cerrar lobbies y preparar matches derivados. |
| `ChatRepositoryRoom` | `IMessageDao`, `TransactionRunner`. | Observar mensajes (`observarMensajes`), insertar nuevos (`enviarMensaje`) y actualizar estados de lectura. |
| `EventoRepositoryRoom` | `IEventoDao`. | Sincronizar campañas (`sincronizarEventos`), listar activas y marcar completadas. |
| `SettingsRepositoryDataStore` | `Context` (DataStore Preferences). | `obtenerPreferencias`, `observar`, `actualizar` sobre `GameSettings`. |
| `SyncRepositoryRoom` | `IOutboxDao`, `ITombstoneDao`, `IIdMapDao`, `TransactionRunner`. | Gestionar colas outbox, mapear IDs servidor/cliente y limpiar registros procesados. |
| `VictoryRepositoryRoom` | `IVictoryLocationDao`. | Guardar y consultar celebraciones (`guardarUbicacion`, `obtenerRecientes`). |

Cada clase convierte entidades a modelos usando los mapeadores del subpaquete `data.local.mapper` y devuelve `Result` con `AgentError` para mantener la trazabilidad de fallos.