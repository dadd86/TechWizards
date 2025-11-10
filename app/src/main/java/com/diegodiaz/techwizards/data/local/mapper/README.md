# `data.local.mapper`

Funciones de extensión que convierten entre entidades Room y modelos de dominio. Mantienen la capa de dominio libre de dependencias de persistencia.

## Mapeadores incluidos

| Archivo | Conversión |
| --- | --- |
| `UsuarioLocalMapper.kt` | `UsuarioEntity` ↔ `Usuario`.
| `MonederoLocalMapper.kt` | `MonederoEntity` ↔ `Monedero`.
| `PartidaLocalMapper.kt` | `PartidaEntity`/`PartidaConUsuarioEntity` ↔ `Partida`.
| `MatchLocalMapper.kt` | `MatchEntity` ↔ `Match`.
| `MatchEventLocalMapper.kt` | `MatchEventEntity` ↔ `MatchEvent`.
| `MatchParticipantLocalMapper.kt` | `MatchParticipantEntity` ↔ `MatchParticipant`.
| `MatchScoreLocalMapper.kt` | `MatchScoreEntity` ↔ `MatchScore`.
| `LobbyLocalMapper.kt` | `LobbyEntity` ↔ `Lobby`.
| `MessageLocalMapper.kt` | `MessageEntity` ↔ `Message`.
| `EventoLocalMapper.kt` | `EventoEntity` ↔ `Evento`.
| `OutboxLocalMapper.kt` | `OutboxEntity` ↔ `Outbox`.
| `TombstoneLocalMapper.kt` | `TombstoneEntity` ↔ `Tombstone`.
| `IdMapLocalMapper.kt` | `IdMapEntity` ↔ `IdMap`.
| `VictoryLocationLocalMapper.kt` | `VictoryLocationEntity` ↔ `VictoryLocation`.
| `PartidaLocalMapper.kt` | Genera resúmenes listos para UI con alias y delta de monedas.

Cada archivo expone funciones `toDomain()` y `toEntity()` o equivalentes, que son usadas por los repositorios antes de devolver resultados a la capa superior.