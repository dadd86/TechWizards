# `integration.victory`

Agrupa servicios y modelos vinculados a celebraciones tras una victoria.

## Componentes

| Clase | Función |
| --- | --- |
| `victoryCelebrationPayload` | Modelo con alias, monedas obtenidas y marca de tiempo de la victoria. Incluye `fromPartida` para generar el payload desde el dominio. |
| `victoryCelebrationService` (`WorkManagerVictoryCelebrationService`) | Encola celebraciones en segundo plano con *backoff* exponencial y delega en el worker. |
| `victoryCelebrationWorker` | Ejecuta la celebración: guarda evento en calendario, crea captura en galería y lanza notificación localizada con `DecentralizedLogger`. |

Se recomienda que el servicio consuma las ubicaciones almacenadas mediante `VictoryRepositoryRoom` y utilice `DecentralizedLogger` para auditar ejecuciones sin exponer PII.