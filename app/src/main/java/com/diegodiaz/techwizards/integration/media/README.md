# `integration.media`

Punto de extensión para reproducir música o efectos sonoros en segundo plano.

## Clases

| Clase | Estado actual | Futuras responsabilidades |
| --- | --- | --- |
| `musicPlaybackService` | Stub sin implementación. | Conectar con `MediaBrowserServiceCompat` o `ExoPlayer` para reproducir playlists, reaccionar a eventos de victoria y respetar AudioFocus. |
| `musicPlaybackController` | Stub sin implementación. | Exponer API sencilla para la UI (`play(uri)`, `pause()`, `toggle()`) delegando en el servicio y actualizando `GameSettings`. |

Al implementar, seguir las políticas de privacidad indicadas en `AGENTS.md` (sin PII en logs) y usar `DecentralizedLogger` para telemetría.