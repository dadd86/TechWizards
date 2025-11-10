# `data.result`

Modela errores específicos de la capa de datos. `DataError` permite traducir excepciones de Room/DataStore en variantes controladas (`NotFound`, `Validation`, `Database`, `Unknown`) antes de mapearlas a `AgentError`.

Los repositorios convierten `DataError` en `Result.Err` para preservar el contexto al subir a la capa de dominio.