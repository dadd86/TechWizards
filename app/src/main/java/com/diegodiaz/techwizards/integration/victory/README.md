# `integration.victory`

Agrupa servicios y modelos vinculados a celebraciones tras una victoria.

## Componentes

| Clase | Función |
| --- | --- |
| `victoryCelebrationPayload` | Datos que describen la celebración (p.ej. ID de match, recursos multimedia, ubicación). Actualmente es un `data class` vacío listo para ampliarse. |
| `victoryCelebrationService` | Servicio placeholder que orquestará la reproducción de animaciones, sonidos y la persistencia en `VictoryRepository`. |

Se recomienda que el servicio consuma las ubicaciones almacenadas mediante `VictoryRepositoryRoom` y utilice `DecentralizedLogger` para auditar ejecuciones sin exponer PII.