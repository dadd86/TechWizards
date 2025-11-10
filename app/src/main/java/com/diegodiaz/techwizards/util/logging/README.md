# `util.logging`

Sistema de logging desacoplado que permite enrutar mensajes a múltiples destinos y aplicar políticas anti-PII.

## Componentes

| Elemento | Función |
| --- | --- |
| `DecentralizedLogger` | Punto central para registrar mensajes (`v`, `d`, `i`, `w`, `e`). Gestiona sinks registrados, nivel mínimo (`setMinLevel`) y mascarado con expresiones regulares (`addPiiMask`). |
| `LogSink` | Interfaz que define `log(level, tag, message, throwable)` para implementar nuevos destinos. |
| `AndroidLogSink` | Sink que delega en `android.util.Log` usando los niveles de `LogLevel`. |
| `FileLogSink` | Sink que escribe en archivos locales (respetando contexto de `App`). Ideal para auditoría offline. |
| `LogLevel` | Enum tipado con prioridad Android y etiqueta textual. Incluye fábricas `fromAndroidPriority` y `fromLabel`. |

## Uso típico

```kotlin
DecentralizedLogger.registerSink(AndroidLogSink())
DecentralizedLogger.registerSink(FileLogSink(context))
DecentralizedLogger.setMinLevel(LogLevel.INFO)
DecentralizedLogger.addPiiMask(Regex("[0-9a-fA-F-]{6,}"))
DecentralizedLogger.i("ControladorPartida", "Victoria detectada")
```

Todos los mensajes deben llegar ya sanitizados; cualquier PII residual se enmascara según las regex configuradas.