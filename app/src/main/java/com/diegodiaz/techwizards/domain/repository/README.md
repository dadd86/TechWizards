# `domain.repository`

Interfaces que definen el acceso a datos del dominio. Permiten intercambiar la implementación (Room, red, mocks) sin modificar la lógica de negocio.

## Interfaces y métodos principales

| Repositorio | Métodos clave | Descripción |
| --- | --- | --- |
| `JuegoRepository` | `observarSaldo`, `observarHistorial`, `observarMonedero`, `lanzarDado`, `inicializarMonedas`, `observeSaldoRx`, `cargarUsuarioRx`, `inicializarMonedasRx`. | Orquesta la economía básica (saldo, historial de partidas) tanto en Flows como en RxJava. |
| `MatchRepository` | `upsertMatch`, `registrarEvento`, `obtenerHistorial`, `guardarScore`. | Gestiona partidas multijugador y su auditoría. |
| `LobbyRepository` | Creación/actualización de lobbies, invitaciones y filtrado por estado. | Facilita coordinar jugadores antes del match. |
| `ChatRepository` | Observación de mensajes, envío y marcado de lectura. | Permite componer experiencias sociales dentro de partidas. |
| `EventoRepository` | `sincronizarEventos`, `obtenerEventos`, `marcarCompletado`. | Maneja contenido dinámico con fechas de vigencia. |
| `UsuarioRepository` | `obtenerUsuarioPrincipal`, `guardar`, `actualizarAlias`. | Aísla el perfil local del jugador. |
| `SyncRepository` | `programar`, `procesarOutbox`, `registrarTombstone`, `guardarIdMap`. | Implementa el patrón *offline-first*. |
| `VictoryRepository` | `guardarUbicacion`, `obtenerRecientes`. | Soporta celebraciones multimedia tras una victoria. |
| `SettingsRepository` | `obtenerPreferencias`, `observar`, `actualizar`. | Centraliza preferencias en DataStore. |

Cada repositorio retorna `Result` y `AgentError` para estandarizar la propagación de fallos. Las implementaciones se encuentran bajo `data/repository/impl`.