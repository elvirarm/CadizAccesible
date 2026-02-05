package com.example.cadizaccesible.data.db

import androidx.room.TypeConverter
import com.example.cadizaccesible.data.reports.EstadoIncidencia
import com.example.cadizaccesible.data.reports.Gravedad
import com.example.cadizaccesible.data.users.RolUsuario

/**
 * Clase encargada de convertir tipos de datos personalizados en tipos que Room pueda persistir.
 * * Dado que SQLite no admite tipos complejos o Enums de forma nativa, estas funciones
 * transforman los objetos del dominio a [String] para su almacenamiento y viceversa
 * para su recuperación.
 */
class Converters {

    // --- Conversiones para RolUsuario ---

    /** Convierte el Enum [RolUsuario] a su nombre en formato [String] para la base de datos. */
    @TypeConverter
    fun rolToString(v: RolUsuario): String = v.name

    /** Recupera el valor del Enum [RolUsuario] a partir del nombre almacenado en la base de datos. */
    @TypeConverter
    fun stringToRol(v: String): RolUsuario = RolUsuario.valueOf(v)


    // --- Conversiones para Gravedad ---

    /** Convierte el Enum [Gravedad] a [String]. */
    @TypeConverter
    fun gravedadToString(v: Gravedad): String = v.name

    /** Convierte el [String] de la base de datos de nuevo al Enum [Gravedad]. */
    @TypeConverter
    fun stringToGravedad(v: String): Gravedad = Gravedad.valueOf(v)


    // --- Conversiones para EstadoIncidencia ---

    /** Convierte el Enum [EstadoIncidencia] a [String]. */
    @TypeConverter
    fun estadoToString(v: EstadoIncidencia): String = v.name

    /** Convierte el [String] de la base de datos de nuevo al Enum [EstadoIncidencia]. */
    @TypeConverter
    fun stringToEstado(v: String): EstadoIncidencia = EstadoIncidencia.valueOf(v)
}