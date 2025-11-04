package com.diegodiaz.techwizards.data.local

class ResultadoConverters {
    @TypeConverter fun toResultado(value: String): Resultado = Resultado.valueOf(value)
    @TypeConverter fun fromResultado(value: Resultado): String = value.name
}