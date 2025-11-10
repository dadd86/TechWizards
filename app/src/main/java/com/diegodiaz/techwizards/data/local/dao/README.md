# `data.local.dao`

Interfaces DAO de Room que encapsulan consultas SQL y operaciones CRUD. Todas usan anotaciones de Room (`@Dao`, `@Query`, `@Insert`, `@Update`, `@Delete`) y devuelven tipos reactivos (`Flow`, `Flowable`, `Completable`) cuando es necesario.

## Principales DAOs

| DAO | Responsabilidad | Operaciones |
| --- | --- | --- |
| `IUsuarioDao` | Gestiona usuarios locales. | `getByNumero`, `upsert`, `upsertSuspend`. |
| `IMonederoDao` | Saldo del monedero. | `observeSaldo`, `upsert`, `actualizarSaldo`, `getMonederoSimple`. |
| `IPartidaDao` | Historial de partidas con alias. | `historial` (Rx), `observarHistorial` (Flow), `insert`, `insertar`, `borrarTodo`. |
| `IMatchDao`, `IMatchEventDao`, `IMatchParticipantDao`, `IMatchScoreDao` | Persistencia de partidas multijugador. | `upsert`, `byMatch`, `clearByMatch`, `insertAll`. |
| `ILobbyDao` | Gestión de salas lobby. | `upsert`, `getActivas`, `deleteById`, `buscarPorCodigo`. |
| `IMessageDao` | Mensajería. | `obtenerPorCanal`, `insert`, `marcarLeido`. |
| `IEventoDao` | Eventos temporales. | `insertAll`, `getActivos`, `marcarCompletado`. |
| `IOutboxDao`, `ITombstoneDao`, `IIdMapDao` | Sincronización offline. | `enqueue`, `pending`, `clear`, `mapFor`. |
| `IVictoryLocationDao` | Celebraciones. | `insert`, `listarRecientes`. |

## Patrones de uso

* Las consultas complejas usan `JOIN` para traer alias relacionados sin lógica adicional en Kotlin.
* Muchas funciones exponen tanto versión RxJava como `Flow` para compatibilidad con controladores existentes.
* Las operaciones críticas (ej. `insert`) se combinan con `TransactionRunner` en la capa de repositorio para garantizar atomicidad.