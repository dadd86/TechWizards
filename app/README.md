# Paquete `com.diegodiaz.techwizards.core`

Encapsula utilidades de dominio compartidas: localizador de servicios, modelos comunes y casos de uso que coordinan repositorios.

## Estructura

* `ServiceLocator`: administra instancias únicas de base de datos y repositorios (`JuegoRepositoryRoom`, `MatchRepositoryRoom`, `SettingsRepositoryDataStore`, `VictoryRepositoryRoom`).
* Subpaquete `common`: define tipos base para errores y resultados.
* Subpaquete `usecases`: agrupa orquestadores que exponen operaciones de dominio listas para la UI.

## Componentes destacados

| Elemento | Función | Detalles |
| --- | --- | --- |
| `ServiceLocator` | Inicializa Room usando `BaseDeDatos.get(context)` y expone DAOs/repositories pregenerados vía `lazy`. | `init(context)` debe llamarse desde `App.onCreate`. |
| `Result` | Sello genérico `Ok`/`Err` para propagar datos o `AgentError`. | Facilita manejo funcional de errores entre capas. |
| `AgentError` | Modela fallas recuperables para UI/telemetría. | Se usa como `E` en `Result`. |
| `ObtenerResumenJugadorUseCase` | Suspende sobre `UsuarioRepository` para recuperar el jugador principal. | Ejecuta en `Dispatchers.IO`. |
| `RegistrarLanzamientoUseCase` | Coordina `JuegoRepository` para registrar tiradas y emitir eventos de victoria. | Encapsula lógica de negocio alrededor de `Partida`. |
| `ObservarPreferenciasUseCase` / `ActualizarPreferenciasUseCase` | Orquesta lectura/escritura de `GameSettings` mediante `SettingsRepository`. | Expone `Flow<GameSettings>` y suspende `invoke(settings)` respectivamente. |
| `ObtenerHistorialPartidasUseCase` | Consulta partidas recientes desde `JuegoRepository`. | Permite parametrizar límite de resultados. |
| `RegistrarEventoMatchUseCase` | Persistencia de `MatchEvent` delegada a `MatchRepository`. | Devuelve `Result<Unit, AgentError>`. |
| `RegistrarUbicacionVictoriaUseCase` | Guarda ubicaciones de celebración vía `VictoryRepository`. | Útil para integraciones multimedia. |

## Dependencias

Los casos de uso dependen de interfaces del paquete `domain.repository` y operan sobre modelos del paquete `domain.model`. La UI invoca estos casos a través de los controladores `ui/controller/*`.