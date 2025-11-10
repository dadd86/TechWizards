# Paquete `com.diegodiaz.techwizards.domain`

Define los contratos puros del dominio: modelos inmutables y repositorios que abstraen las fuentes de datos. No contiene código Android ni dependencias de infraestructura.

## Modelos (`domain.model`)

| Modelo | Propósito | Campos clave |
| --- | --- | --- |
| `Usuario` | Jugador local almacenado en Room. | `numero`, `alias`, `fechaAltaMs`, `monedas`, `ganoUltimaPartida`, `firebaseUid`. |
| `Monedero` | Saldo de monedas virtuales asociado a un usuario. | `id`, `usuarioNumero`, `saldo`. |
| `Partida` | Registro de una tirada de dado. | `id`, `aliasJugador`, `fecha`, `resultado`, `deltaMonedas`. |
| `GameSettings` | Preferencias del cliente (audio, animaciones, idioma, tema). | Flags booleanas + `selectedMusicUri`, `selectedLanguageTag`. |
| `Lobby` / `Match` / `MatchParticipant` / `MatchScore` | Entidades para partidas multijugador y sus participantes. | Identificadores, estado (`estado`/`modo`), marcas de tiempo y puntuaciones. |
| `MatchEvent` | Evento inmutable asociado a un match (ej. un turno o acción). | `id`, `matchId`, `type`, `payload`, `createdAt`. |
| `Message` | Mensajería interna para chats. | `id`, `roomId`, `authorAlias`, `contenido`, `timestamp`. |
| `Evento` | Eventos temporales dentro del juego (misiones, desafíos). | `id`, `nombre`, `descripcion`, `fechaInicio/Fin`, `completado`. |
| `VictoryLocation` | Coordenadas o referencia de celebraciones. | `id`, `matchId`, `lat`, `lon`, `capturedAt`. |
| `Outbox`, `Tombstone`, `IdMap` | Soporte offline-first: colas de sincronización, marcadores de borrado y mapeo de IDs remotos. |

## Repositorios (`domain.repository`)

| Interfaz | Responsabilidades | Operaciones relevantes |
| --- | --- | --- |
| `JuegoRepository` | Gestiona usuario, monedero e historial de partidas. | `observarSaldo`, `observarHistorial`, `lanzarDado`, `inicializarMonedas`, variantes Rx (`observeSaldoRx`, `cargarUsuarioRx`, `inicializarMonedasRx`). |
| `MatchRepository` | Persiste partidas multijugador y eventos. | `upsertMatch`, `registrarEvento`, `obtenerHistorial`, `guardarScore`. |
| `LobbyRepository` | Maneja salas previas al match. | Métodos de creación, invitaciones y actualización (ver implementación Room). |
| `ChatRepository` | Administra mensajes del chat en salas o matches. | Observación de mensajes, envío y marcado de leídos. |
| `EventoRepository` | Sincroniza eventos temporales del juego. | `sincronizar`, `obtenerActivos`, `marcarCompletado`. |
| `UsuarioRepository` | Provee acceso al perfil local. | `obtenerUsuarioPrincipal`, `actualizarAlias`, `registrarAlta`. |
| `SyncRepository` | Coordina colas outbox/tombstones para sincronización con backend. | `enviarPendientes`, `programarSync`, `guardarIdMap`. |
| `VictoryRepository` | Registra ubicaciones y datos para celebraciones. | `guardarUbicacion`, `obtenerRecientes`. |
| `SettingsRepository` | Persistencia de `GameSettings`. | `obtenerPreferencias`, `observar`, `actualizar`. |

> Las implementaciones concretas de estos contratos residen en `data/repository/impl`.