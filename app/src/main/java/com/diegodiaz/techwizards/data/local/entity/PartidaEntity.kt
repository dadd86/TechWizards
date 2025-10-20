package com.diegodiaz.techwizards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room para partidas locales.
 *
 * @property id Identificador principal de la partida.
 * @property matchId Match sincronizado, si existe.
 * @property mapaId Mapa asociado a la partida.
 * @property estado Estado textual persistido.
 * @property startedAtMs Marca de inicio.
 * @property finishedAtMs Marca de fin.
 * @property ganadorNum Usuario ganador, si fue determinado.
 * @security
 * - Mantener consistencia entre `estado` y el dominio.
 * - Registrar auditoría cuando se actualice el ganador.
 */
@Entity(
    tableName = "Partida",
    indices = [Index(value = ["matchId"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = MatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["matchId"],
            onDelete = ForeignKey.SET_NULL,
        ),
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["numero"],
            childColumns = ["ganadorNum"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
)
data class PartidaEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "matchId")
    val matchId: String?,
    @ColumnInfo(name = "mapaId")
    val mapaId: String?,
    @ColumnInfo(name = "estado")
    val estado: String,
    @ColumnInfo(name = "startedAtMs")
    val startedAtMs: Long?,
    @ColumnInfo(name = "finishedAtMs")
    val finishedAtMs: Long?,
    @ColumnInfo(name = "ganadorNum")
    val ganadorNum: Long?,
)