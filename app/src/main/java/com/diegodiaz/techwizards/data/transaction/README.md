# `data.transaction`

Abstracciones para ejecutar operaciones atómicas.

## Componentes

| Elemento | Descripción |
| --- | --- |
| `TransactionRunner` | Interface suspend que ejecuta un bloque dentro de una transacción y devuelve el resultado genérico. |
| `RoomTransactionRunner` | Implementación que usa `RoomDatabase.withTransaction` para asegurar atomicidad. |
| `DataError` (en este paquete) | Enumera fallas específicas de transacciones: `Conflict`, `Timeout`, `Unknown` (ver archivo `DataError.kt`). |

Los repositorios inyectan un `TransactionRunner` cuando necesitan combinar múltiples inserciones/actualizaciones (por ejemplo, al cerrar un `Match` y actualizar scores/monederos en la misma transacción).