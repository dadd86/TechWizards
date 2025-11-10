# Subpaquete `core.common`

Define contratos compartidos para representar resultados y errores que fluyen entre capas.

## Tipos disponibles

| Tipo | Uso | Notas |
| --- | --- | --- |
| `AgentError` | Jerarquía sellada de fallas conocidas (red, timeout, validación, base local, desconocido). | Permite a la UI reaccionar de forma uniforme mostrando mensajes o solicitando reintentos. |
| `Result<T, E>` | Contenedor genérico con variantes `Ok` y `Err`. | Se usa para envolver respuestas de repositorio/casos de uso y transportar `AgentError`. |

## Integración

* Los repositorios (`domain.repository.*`) devuelven `Result` para evitar excepciones no controladas.
* Los casos de uso bajo `core.usecases` transforman errores específicos en `AgentError` antes de llegar a la UI.