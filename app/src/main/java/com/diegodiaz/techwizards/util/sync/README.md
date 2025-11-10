# `util.sync`

Preparado para tareas de sincronización en segundo plano.

## Componentes

| Clase | Descripción |
| --- | --- |
| `SyncWorker` | Stub que se convertirá en un `CoroutineWorker`/`ListenableWorker` de WorkManager encargado de vaciar la cola `Outbox`, enviar tombstones y actualizar `IdMap`. |

Sugerencias:

* Inyectar `SyncRepository` y `DecentralizedLogger` para telemetría.
* Respetar cuotas y políticas de reintento exponiendo `Result.success()`/`Result.retry()` según corresponda.