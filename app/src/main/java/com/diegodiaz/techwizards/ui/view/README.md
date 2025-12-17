# `ui.view`

Pantallas Jetpack Compose y el `NavGraph` principal de Tech Wizards.

## Infraestructura

| Archivo | Rol |
| --- | --- |
| `AppRoot.kt` | Monta el `Scaffold` principal, crea `NavController` y delega en `NavGraph`. |
| `NavGraph.kt` | Define destinos (`bienvenida`, `menu`, `partida`, `historial`, `ajustes`, `ayuda`), inicializa el jugador en Room y crea ViewModels (`ControladorPartida`, `ControladorAjustes`). |

## Pantallas

| Pantalla | Descripción | Callbacks principales |
| --- | --- | --- |
| `PantallaBienvenida` | Solicita nombre del jugador y dispara inicialización de usuario/monedero en Room antes de navegar al menú. | `onJugar(nombre)`. |
| `PantallaMenu` | Menú principal con accesos a jugar, historial, ajustes, ayuda y salir (incluye confirmación). | `onJugar`, `onHistorial`, `onAjustes`, `onAyuda`. |
| `PantallaPartida` | Muestra saldo, último resultado y dados interactivos; escucha `JuegoUiEvent` para celebraciones. | `onElegirNumero`, `onVolverAlMenu`. |
| `PantallaHistorial` | Lista partidas recientes usando `ControladorPartida.historial`. | `onVolverAlMenu`. |
| `PantallaAjustes` | Ajustes multimedia y de accesibilidad (tema, música, SFX, animaciones, notificaciones, idioma, pista personalizada). Consume eventos de `ControladorAjustes` para manejar `MusicPlaybackController`. | `onToggleTheme`, `onToggleMusic`, `onToggleSfx`, `onToggleAnimations`, `onToggleNotifications`, `onElegirPista`, `onSeleccionIdioma`, `onVolverAlMenu`. |
| `PantallaChat` | Interfaz de chat simple respaldada por `ControladorChat`. | `onEnviar`, `onCambiarTexto`. |
| `PantallaLobby` | Gestión visual de lobbies (crear, seleccionar existentes). | `onCrearLobby`, `onSeleccionLobby`, `onVolver`. |
| `PantallaMatch` | Detalle de un match en curso: participantes, puntuaciones y acciones rápidas. | `onIniciar`, `onFinalizar`, `onSumarPuntos`. |
| `PantallaEventos` | Muestra eventos temporales y su estado de progreso. | `onVolver`, `onMarcarCompletado`. |
| `PantallaAyuda` | WebView multilingüe (EN/DE) que carga ayuda desde `assets/help`, registra la selección en `DecentralizedLogger` y evita contenido dinámico inseguro. | `onVolver` si aplica. |

Todas las pantallas usan el helper `Responsive` para adaptar tamaños según el espacio disponible y se apoyan en el tema definido en `ui/theme`.

## Pruebas manuales

### `PantallaMatch` (online)
1. Iniciar sesión con dos jugadores y crear un match nuevo. Confirmar que la UI muestra estado `PENDING` y el botón de lanzar está deshabilitado.
2. Marcar ambos jugadores como listos desde la pantalla. Verificar que el estado cambie a `ACTIVE` y que el botón de lanzamiento se habilite para ambos.
3. Lanzar dados hasta que se registre un ganador y se actualicen los puntajes. Al finalizar la ronda, confirmar que la pantalla refleje el estado `FINISHED` y los scores finales.