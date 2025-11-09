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