package com.diegodiaz.techwizards.data.local

import androidx.room.TypeConverter
import com.diegodiaz.techwizards.data.local.entity.Resultado

/**
 * Conversores de Room para almacenar el enum [Resultado] como texto.
 */
class ResultadoConverters {

    @TypeConverter
    fun toResultado(value: String): Resultado = Resultado.valueOf(value)

    @TypeConverter
    fun fromResultado(value: Resultado): String = value.name
}