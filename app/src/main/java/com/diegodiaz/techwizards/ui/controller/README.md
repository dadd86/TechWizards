# `ui.controller`

ViewModels y controladores que sirven de puente entre los casos de uso y las pantallas Compose.

## Patrones comunes

* Estado expuesto mediante `StateFlow` (`ui`, `historial`) y eventos puntuales con `SharedFlow` (ver `ControladorPartida`).
* Métodos auxiliares `limpiarError()` en varios controladores para reiniciar el estado de error.
* Factories (`SimpleVmFactory`) para instanciar ViewModels con parámetros personalizados.

## Controladores disponibles

| Clase | Propósito | Funciones clave |
| --- | --- | --- |
| `ControladorPartida` | Coordina la pantalla de juego principal. | Combina saldo + historial + preferencias (`ui`), registra lanzamientos (`lanzar`, `elegirNumero`), emite `JuegoUiEvent.Victoria`. |
| `ControladorHistorial` | Recupera partidas recientes desde `JuegoRepository`. | Expone `historial: StateFlow<List<Partida>>` usando `stateIn`. |
| `ControladorMatch` | Administra un match multijugador en memoria. | `crearMatch`, `addParticipante`, `iniciar`, `finalizar`, `sumarPuntos`. |
| `ControladorLobby` | Gestiona salas de espera locales. | `crearLobby`, `seleccionar`, `limpiarError`. |
| `ControladorChat` | Controla mensajes de chat durante un match. | `escribir`, `enviar`, `limpiarError`; mantiene `ChatUiState`. |
| `ControladorSync` | Simula la cola outbox para sincronización. | `enqueue`, `marcarExitoso`, `marcarIntentoFallido`, `limpiarError`. |
| `controladorAjustes` | Placeholder para lógica de ajustes. | Se espera gestione toggles y persistencia de `GameSettings`. |
| `SimpleVmFactory` | `ViewModelProvider.Factory` simple para construir controladores con lambdas. | `create` castea el resultado de `create()`.

Estos controladores se instancian normalmente desde `NavGraph` o composables, obteniendo repositorios a través del `ServiceLocator`.