package com.diegodiaz.techwizards.data.local


import androidx.room.TypeConverter
import com.diegodiaz.techwizards.data.local.entity.Resultado

class EnumConverters {
    @TypeConverter
    fun fromResultado(value: Resultado?): String? = value?.name

    @TypeConverter
    fun toResultado(value: String?): Resultado? =
        value?.let { runCatching { Resultado.valueOf(it) }.getOrNull() }
}
