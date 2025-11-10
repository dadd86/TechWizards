# Paquete `com.diegodiaz.techwizards.data`

Implementa la capa de datos respaldada principalmente por Room y DataStore. Aquí viven las entidades persistidas, sus DAOs, mapeadores hacia el dominio y las implementaciones concretas de los repositorios definidos en `domain`.

## Subpaquetes

| Subpaquete | Rol | Componentes |
| --- | --- | --- |
| `local` | Persistencia Room: entidades, DAOs, convertidores y base de datos. | `BaseDeDatos`, `EnumConverters`, `ResultadoConverters`, entidades `*Entity`, DAOs `I*Dao`, mapeadores `*LocalMapper`. |
| `repository.impl` | Implementaciones concretas de los repositorios de dominio. | `JuegoRepositoryRoom`, `MatchRepositoryRoom`, `SettingsRepositoryDataStore`, `VictoryRepositoryRoom`, `ChatRepositoryRoom`, etc. |
| `transaction` | Abstracciones de ejecución transaccional. | `TransactionRunner`, `RoomTransactionRunner`, tipos de error `DataError`. |
| `result` | Modelos de error específicos de datos. | `DataError` con variantes para red, base, esquema. |
| `infra.di` | Módulos de inyección manual. | `moduloPartidas` expone factories de repositorios/DAOs. |

## Repositorios Room destacados

| Clase | Responsabilidad | Métodos |
| --- | --- | --- |
| `JuegoRepositoryRoom` | Gestiona usuarios, monedero y partidas. | `observarSaldo`, `observarHistorial`, `observarMonedero`, `inicializarMonedas`, `lanzarDado` + APIs Rx. |
| `MatchRepositoryRoom` | Crea/actualiza matches y registra eventos/scores. | `upsertMatch`, `registrarEvento`, `obtenerHistorial`, `guardarScore`. |
| `LobbyRepositoryRoom` | Mantiene lobbies, sus estados e invitaciones. | Métodos CRUD según estado (`crear`, `unirse`, `actualizarEstado`). |
| `EventoRepositoryRoom` | Persiste campañas/eventos temporales. | `sincronizarEventos`, `obtenerActivos`. |
| `ChatRepositoryRoom` | Maneja mensajes en canales/matches. | `observarMensajes`, `enviarMensaje`, `marcarLeidos`. |
| `SettingsRepositoryDataStore` | Persiste `GameSettings` con DataStore Preferences. | `obtenerPreferencias`, `observar`, `actualizar`. |
| `SyncRepositoryRoom` | Opera sobre tablas `Outbox`, `Tombstone`, `IdMap`. | `registrarOperacion`, `procesarPendientes`, `mapearId`. |
| `VictoryRepositoryRoom` | Guarda celebraciones/ubicaciones de victoria. | `guardarUbicacion`, `listarRecientes`. |

## Base de datos `Room`

`BaseDeDatos` declara la lista completa de entidades y DAOs, aplica migraciones (`MIGRATION_1_2`, `MIGRATION_2_3`) y un `RoomCallbackPragmas` para configurar pragmas de seguridad/performance. Los esquemas exportados viven en `app/schemas`.

## Mapeadores

Los archivos `*LocalMapper.kt` convierten entre entidades (`*Entity`) y modelos de dominio (`domain.model`). Ejemplos: `MatchLocalMapper`, `UsuarioLocalMapper`, `PartidaLocalMapper`, `VictoryLocationLocalMapper`.

## Ejecución transaccional

`TransactionRunner` define una API suspend para ejecutar bloques atómicos; `RoomTransactionRunner` implementa el contrato usando `RoomDatabase.withTransaction`. Los repositorios la usan para garantizar consistencia en escrituras complejas.