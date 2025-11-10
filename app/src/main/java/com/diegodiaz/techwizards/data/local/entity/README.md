# `data.local.entity`

Define las entidades Room que representan tablas de la base de datos local. Cada `data class` está anotada con `@Entity` y mapea 1:1 el esquema descrito en `SQL.primer.sql`.

## Categorías

* **Core del jugador**: `UsuarioEntity`, `MonederoEntity`, `PartidaEntity`, `PartidaConUsuarioEntity` (POJO para consultas con join).
* **Eventos y lobby**: `EventoEntity`, `LobbyEntity`, `MatchEntity`, `MatchEventEntity`, `MatchParticipantEntity`, `MatchScoreEntity`.
* **Mensajería**: `MessageEntity` para chat interno.
* **Offline-first**: `OutboxEntity`, `TombstoneEntity`, `IdMapEntity` almacenan operaciones pendientes y referencias cruzadas.
* **Celebraciones**: `VictoryLocationEntity` registra ubicaciones/metadata de victorias.

Los campos usan tipos primitivos, enums convertidos mediante `EnumConverters` y claves foráneas con `onDelete` apropiados para mantener integridad.