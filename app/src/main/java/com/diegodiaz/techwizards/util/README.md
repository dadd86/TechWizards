# Paquete `com.diegodiaz.techwizards.util`

Herramientas transversales usadas por varias capas: logging, sincronización, tiempo y generación de IDs.

## Subpaquetes

| Subpaquete | Responsabilidad |
| --- | --- |
| `logging` | Logger desacoplado con sinks configurables y niveles dinámicos. |
| `sync` | Workers y tareas periódicas para sincronización offline. |
| `time` | Utilidades de formato/parseo de fechas. |
| `ids` | Generación de identificadores estables (UUID). |

Cada subpaquete expone clases independientes sin depender de Android salvo cuando es imprescindible (ej. `FileLogSink` necesita `Context`).