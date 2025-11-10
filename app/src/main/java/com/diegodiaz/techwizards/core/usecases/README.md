# Subpaquete `core.usecases`

Expone casos de uso que orquestan uno o más repositorios del dominio. Cada clase se declara como un invocable (`operator fun invoke`) para integrarse fácilmente en controladores y workers.

## Casos de uso

| Caso de uso | Entrada | Salida | Descripción |
| --- | --- | --- | --- |
| `ObtenerResumenJugadorUseCase` | — | `Result<Usuario, AgentError>` | Consulta al `UsuarioRepository` para recuperar el jugador activo y mostrarlo en el menú principal. |
| `ObtenerHistorialPartidasUseCase` | `usuarioId: String`, `limite: Int` | `Result<List<Partida>, AgentError>` | Delegado en `JuegoRepository.observarHistorial` para poblar listados recientes. |
| `RegistrarLanzamientoUseCase` | `usuarioId: String` | `Result<Partida, AgentError>` | Ejecuta `JuegoRepository.lanzarDado`, actualiza el saldo y devuelve la partida creada para efectos UI. |
| `ObservarPreferenciasUseCase` | — | `Flow<GameSettings>` | Envuelve `SettingsRepository.observar()` exponiendo preferencias reactivas para toggles de UI. |
| `ObtenerPreferenciasUseCase` | — | `Result<GameSettings, AgentError>` | Lectura puntual de ajustes almacenados en DataStore. |
| `ActualizarPreferenciasUseCase` | `GameSettings` | `Result<Unit, AgentError>` | Persiste la configuración recibida en DataStore asegurando coherencia de flags. |
| `RegistrarEventoMatchUseCase` | `MatchEvent` | `Result<Unit, AgentError>` | Llama a `MatchRepository.registrarEvento` para auditar acciones en partidas multijugador. |
| `RegistrarUbicacionVictoriaUseCase` | `VictoryLocation` | `Result<Unit, AgentError>` | Usa `VictoryRepository.guardarUbicacion` para registrar dónde ocurrió una victoria. |

## Patrones

* Todos los casos reciben un `CoroutineDispatcher` inyectable (por defecto `Dispatchers.IO`) para testabilidad.
* Los resultados se devuelven con `Result` para que la UI distinga éxito/fracaso sin excepciones.