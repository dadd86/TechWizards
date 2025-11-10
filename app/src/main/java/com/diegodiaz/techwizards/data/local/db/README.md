# `data.local.db`

Clases relacionadas con la configuración `RoomDatabase`.

## Archivos

| Archivo | Función |
| --- | --- |
| `BaseDeDatos.kt` | Declara `@Database`, lista de entidades, DAOs abstractos y migraciones (`MIGRATION_1_2`, `MIGRATION_2_3`). Expone `get(context)` como singleton seguro. |
| `RoomCallbackPragmas.kt` / `PragmaCallback.kt` | Callbacks para aplicar `PRAGMA foreign_keys=ON`, `journal_mode`, etc., cuando la base abre conexión. |

Estas clases se utilizan desde `ServiceLocator` y `NavGraph` para obtener DAOs listos para los repositorios.