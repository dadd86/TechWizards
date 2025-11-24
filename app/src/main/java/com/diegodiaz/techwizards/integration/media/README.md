# `integration.media`

Servicios y controladores multimedia desacoplados de la UI.

## Clases

| Clase | Rol | Consideraciones |
| --- | --- | --- |
| `musicPlaybackService` | Servicio `START_STICKY` y *foreground* que reproduce música de fondo, gestiona `AudioFocus`, responde a llamadas/alarma (pausa automática) y evita PII en logs vía `DecentralizedLogger`. | Usa `MediaPlayer` con `AudioAttributes.USAGE_GAME`, receptor `ACTION_AUDIO_BECOMING_NOISY` y notificación persistente `music_channel` con textos localizados. |
| `musicPlaybackController` | API ligera para que la UI encienda/apague la música o seleccione pistas desde el dispositivo. | Arranca el servicio con acciones `PLAY_OFFICIAL`, `PLAY_CUSTOM` o `STOP`, y ofrece `applySettings(enabled, uri)` para respetar la pista personalizada almacenada en preferencias. |


Todos los archivos siguen la convención lowerCamelCase y deben documentarse con KDoc en español incluyendo la etiqueta `@security`.