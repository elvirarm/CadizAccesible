package com.example.cadizaccesible.data.db

import androidx.room.TypeConverter
import com.example.cadizaccesible.data.reports.EstadoIncidencia
import com.example.cadizaccesible.data.reports.Gravedad
import com.example.cadizaccesible.data.users.RolUsuario

class Converters {

    @TypeConverter fun rolToString(v: RolUsuario): String = v.name
    @TypeConverter fun stringToRol(v: String): RolUsuario = RolUsuario.valueOf(v)

    @TypeConverter fun gravedadToString(v: Gravedad): String = v.name
    @TypeConverter fun stringToGravedad(v: String): Gravedad = Gravedad.valueOf(v)

    @TypeConverter fun estadoToString(v: EstadoIncidencia): String = v.name
    @TypeConverter fun stringToEstado(v: String): EstadoIncidencia = EstadoIncidencia.valueOf(v)
}
