# `data.local`

Capa Room que define el esquema SQLite y los componentes necesarios para accederlo.

## Componentes

| Tipo | Descripción |
| --- | --- |
| `BaseDeDatos` | `RoomDatabase` que declara todas las entidades (`UsuarioEntity`, `PartidaEntity`, `MatchEntity`, `OutboxEntity`, etc.), DAOs y migraciones (1→2 agrega alias a `Partida`, 2→3 registra `VictoryLocation`). |
| `EnumConverters` / `ResultadoConverters` | `TypeConverter` para mapear enums (`Resultado`, modos de juego, estados) y tipos compuestos a columnas primitivas. |
| `RoomCallbackPragmas` / `PragmaCallback` | Configuración adicional para habilitar `FOREIGN_KEYS`, `JOURNAL_MODE`, etc. |
| Subcarpetas `dao`, `entity`, `mapper` | Separan acceso (`I*Dao`), representación persistente (`*Entity`) y traducciones (`toDomain`, `toEntity`). |

## Flujo de datos

1. Los repositorios (`data.repository.impl`) solicitan DAOs al `ServiceLocator`.
2. Cada DAO ejecuta consultas anotadas con SQL seguro (joins, índices definidos en migraciones).
3. Los mapeadores convierten las entidades en modelos de `domain.model` para aislar la UI de detalles de persistencia.