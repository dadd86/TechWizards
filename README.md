📘 Documentación Unificada – Proyecto TechWizards
🧩 Paquete com.diegodiaz.techwizards.app

La aplicación inicializa el ServiceLocator, registra los sinks de log (Logcat y archivo rotado), fija políticas anti-PII y establece el contenedor de dependencias al arrancar.

MainActivity habilita el modo edge-to-edge, controla el estado de tema claro/oscuro mediante rememberSaveable y delega la navegación en AppRoot dentro del tema TechWizardsTheme.

⚙️ Paquete com.diegodiaz.techwizards.core

ServiceLocator mantiene una instancia singleton de Room, expone DAOs y repositorios (juego, match, settings, victoria), y permite su inicialización con el contexto de la aplicación.

El módulo common define:

AgentError: modelo tipificado de fallos (red, validación, base de datos, timeout, desconocido).

Result: tipo algebraico sellado éxito/error para propagar resultados seguros sin excepciones filtradas.

🧠 Casos de uso

ActualizarPreferenciasUseCase: persiste GameSettings vía SettingsRepository, con logs redactados.

ObtenerPreferenciasUseCase: recupera preferencias desde DataStore en Dispatchers.IO.

ObservarPreferenciasUseCase: stub sin lógica.

ObtenerHistorialPartidasUseCase: valida límites y consulta el repositorio de partidas.

ObtenerResumenJugadorUseCase: devuelve información del jugador principal.

RegistrarEventoMatchUseCase: valida campos, sanea payloads JSON, persiste en Room y encola en outbox con logs redactados.

RegistrarLanzamientoUseCase: registra lanzamientos de dado, ajusta saldo y emite eventos de victoria.

RegistrarUbicacionVictoriaUseCase: interfaz vacía pendiente de implementación.

🧩 Paquete com.diegodiaz.techwizards.domain
Modelos

Define DTOs para eventos, partidas, sincronización y ajustes de jugador.
Incluye:

GameSettings, Monedero, Usuario, Match, MatchEstado, Message, MatchEvent, MatchParticipant, MatchScore, Lobby, LobbyEstado, Partida, Outbox, IdMap, Tombstone, Evento, y victoryLocation (stub).
Cada clase documenta consideraciones de seguridad y serialización.

Repositorios

Publica contratos para chat, eventos, economía, lobby, matches, sincronización, usuario, ajustes y victorias.
Ejemplo:
JuegoRepository, ChatRepository, LobbyRepository, MatchRepository, SyncRepository, UsuarioRepository, VictoryRepository (stub), EventoRepository, SettingsRepository.

🧱 Paquete com.diegodiaz.techwizards.data
Base de datos Room

BaseDeDatos configura todas las entidades, convertidores y migraciones (v1→v3), con callbacks PRAGMA que refuerzan foreign_keys y WAL.
Los EnumConverters y ResultadoConverters gestionan la serialización bidireccional de enums.

DAOs

Interfaces IUsuarioDao, IMonederoDao, IPartidaDao, IEventoDao, ILobbyDao, IMatchDao, IMatchEventDao, IMatchParticipantDao, IMatchScoreDao, IMessageDao, IOutboxDao, IIdMapDao, ITombstoneDao, IVictoryLocationDao (stub) cubren operaciones CRUD y observación reactiva.

Entidades

Reflejan el esquema Room/SQL: UsuarioEntity, MonederoEntity, PartidaEntity, EventoEntity, LobbyEntity, MatchEntity, MessageEntity, OutboxEntity, IdMapEntity, TombstoneEntity, VictoryLocationEntity (stub), entre otras.

Mappers

Incluye convertidores como EventoLocalMapper, UsuarioLocalMapper, LobbyLocalMapper, MatchEventLocalMapper, VictoryLocationLocalMapper (stub) y más, encargados de traducir entre dominio y persistencia.

Transacciones y errores

TransactionRunner y RoomTransactionRunner ejecutan bloques withTransaction en Dispatchers.IO.
DataError tipifica fallos de datos (validación, red, integridad).

Repositorios concretos

JuegoRepositoryRoom, ChatRepositoryRoom, LobbyRepositoryRoom, MatchRepositoryRoom, SyncRepositoryRoom implementan contratos de dominio usando DAOs.

SettingsRepositoryDataStore, VictoryRepositoryRoom son stubs sin implementación.

EventoRepositoryRoom adapta IEventoDao a flujos Rx/coroutines.

Inyección de dependencias

infra.di.moduloPartidas actúa como placeholder para futuras vinculaciones.

🔐 Paquete com.diegodiaz.techwizards.credenciales

Define la interfaz CredentialsStore y el stub EncryptedCredentialsStore como base para almacenamiento seguro de credenciales futuras.

🧰 Paquete com.diegodiaz.techwizards.util

DecentralizedLogger centraliza el registro con sinks Logcat y archivo, aplica niveles tipados (LogLevel) y máscaras PII.
Contiene también:

UuidProvider, DateFormats y SyncWorker: utilidades y workers aún por implementar.

🌐 Paquete com.diegodiaz.techwizards.integration

media: stubs MusicPlaybackController y MusicPlaybackService, pendientes de corrección de nombres e integración con la UI.

victory: VictoryCelebrationPayload y VictoryCelebrationService preparados para futuras animaciones post-victoria mediante WorkManager.

🎨 Paquete com.diegodiaz.techwizards.ui
Navegación y layout

Ruta define destinos sellados del NavHost.
Responsive y UiDims ajustan dimensiones dinámicas según el dispositivo.

Tema

Color, Type y TechWizardsTheme implementan paleta Material 3, tipografías y soporte de colores dinámicos (Android 12+).

Controladores / ViewModels

ControladorPartida: orquesta estado del juego, historial y eventos de victoria.

ControladorLobby: maneja salas de espera y estado local.

ControladorMatch, ControladorHistorial, ControladorChat, ControladorSync: controladores con estados y flujos propios.

controladorAjustes: stub pendiente de implementación.

SimpleVmFactory: crea factories inline genéricas.

Vistas Compose

AppRoot: raíz del NavGraph dentro de un Scaffold.

NavGraph: inicializa Room y repositorios vía ServiceLocator, enruta entre pantallas.

PantallaBienvenida, PantallaMenu, PantallaPartida, PantallaHistorial, PantallaAjustes: vistas principales con diálogos, animaciones, layouts responsivos y control multimedia (aún incompleto).

Pantallas secundarias (PantallaChat, PantallaMatch, PantallaLobby, PantallaEventos, PantallaAyuda): definidas como stubs.

🧪 Pruebas

Incluye:

ExampleUnitTest: verificación básica.

DecentralizedLoggerTest: prueba de registro y mascarado.

VictoryLocationLocalMapperTest: stub vacío.

ExampleInstrumentedTest: verifica nombre de paquete en entorno real.

📂 Recursos y documentación adicional

Ayuda HTML (app/src/main/assets/help/index.html): reglas del juego, celebraciones y privacidad.

SQL/PrimerSQL.sql: esquema completo SQLite (v3) con PRAGMAs, índices y comentarios alineados con entidades Room.


# Módulo `app`

Este módulo contiene la aplicación Android completa de **Tech Wizards**, organizada en capas limpias (dominio, datos, núcleo de casos de uso, utilidades, integración y UI). La configuración de Gradle, el manifiesto y los recursos viven aquí junto con el código Kotlin.

## Directorios principales

| Carpeta | Propósito | Componentes destacados |
| --- | --- | --- |
| `src/main/java/com/diegodiaz/techwizards/app` | Punto de entrada Android. | `App` inicializa el `ServiceLocator` y el logger; `MainActivity` compone el árbol UI con `AppRoot`. |
| `src/main/java/com/diegodiaz/techwizards/core` | Núcleo compartido (service locator, modelos de error y casos de uso). | `ServiceLocator`, paquete `common` con `Result`/`AgentError` y el paquete `usecases` con orquestadores de dominio. |
| `src/main/java/com/diegodiaz/techwizards/domain` | Modelos y contratos de repositorio puros. | Modelos `Usuario`, `Partida`, `Match`, `GameSettings`, etc. Interfaces `JuegoRepository`, `MatchRepository`, `UsuarioRepository`, entre otras. |
| `src/main/java/com/diegodiaz/techwizards/data` | Implementaciones Room/DataStore y mapeadores. | Base `Room`, DAOs, entidades, mapeadores y repositorios `*RepositoryRoom`. |
| `src/main/java/com/diegodiaz/techwizards/ui` | Capas de presentación Jetpack Compose y controladores/ViewModels. | Navegación, controladores (`Controlador*`), screens `Pantalla*`, tema y utilidades responsive. |
| `src/main/java/com/diegodiaz/techwizards/util` | Utilidades técnicas compartidas. | Logger descentralizado, formato de fechas, sincronización y proveedores de IDs. |
| `src/main/java/com/diegodiaz/techwizards/integration` | Integraciones externas locales (multimedia, celebraciones). | Stubs para `musicPlaybackService`, `victoryCelebrationService`, etc. |
| `src/main/java/com/diegodiaz/techwizards/credenciales` | Abstracciones de almacenamiento seguro. | `CredentialsStore` y `EncryptedCredentialsStore`. |

## Flujo de inicialización

1. `App.onCreate` registra los sinks de `DecentralizedLogger`, configura mascarado PII y levanta el `ServiceLocator`.
2. `ServiceLocator.init` crea la base de datos `Room`, expone DAOs y repositorios (`JuegoRepositoryRoom`, `MatchRepositoryRoom`, `SettingsRepositoryDataStore`, `VictoryRepositoryRoom`).
3. `MainActivity` habilita `EdgeToEdge`, inyecta `TechWizardsTheme` y delega a `AppRoot`, que a su vez crea el `NavGraph` Compose.

## Casos de uso y datos

Los casos de uso bajo `core/usecases` combinan repositorios de `domain` y `data` para exponer operaciones de alto nivel a los controladores de UI. Ejemplos: `ObtenerResumenJugadorUseCase`, `RegistrarLanzamientoUseCase`, `ObservarPreferenciasUseCase`.

Los repositorios concretos viven en `data/repository/impl` y aprovechan la capa `data/local`:

* `JuegoRepositoryRoom` sincroniza usuarios, monedero y partidas (`observarHistorial`, `lanzarDado`, `inicializarMonedas`).
* `MatchRepositoryRoom` gestiona partidas multijugador (`upsertMatch`, `registrarEvento`, `guardarScore`).
* `SettingsRepositoryDataStore` persiste `GameSettings` con `DataStore`.
* `VictoryRepositoryRoom` almacena ubicaciones de victoria para celebraciones posteriores.

## Presentación

Los controladores en `ui/controller` componen estado y efectos unidireccionales para las composables de `ui/view`. El patrón típico es:

1. `Controlador*` obtiene repositorios y casos de uso desde el `ServiceLocator`.
2. Se expone un `StateFlow` con datos memoizados (`stateIn`, `combine`).
3. Las screens Compose consumen ese estado mediante `collectAsStateWithLifecycle` o `collectAsState`.

La navegación se centraliza en `ui/view/NavGraph.kt`, donde se declaran rutas (`ui/navigation/Ruta`) y se conectan con cada `Pantalla*`.

## Utilidades destacadas

* `DecentralizedLogger` agrega sinks (`AndroidLogSink`, `FileLogSink`) y aplica mascarado de PII.
* `SyncWorker` coordina sincronización diferida usando WorkManager.
* `UuidProvider` genera identificadores consistentes para entidades offline-first.

## Recursos adicionales

* `schemas/` contiene los esquemas exportados de Room para verificación.
* `SQL.primer.sql` documenta la base SQL original para validaciones manuales.