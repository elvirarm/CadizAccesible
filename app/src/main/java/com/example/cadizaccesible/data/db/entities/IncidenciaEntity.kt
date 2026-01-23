package com.example.cadizaccesible.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cadizaccesible.data.reports.EstadoIncidencia
import com.example.cadizaccesible.data.reports.Gravedad

@Entity(tableName = "incidencias")
data class IncidenciaEntity(
    @PrimaryKey val id: String,
    val emailCreador: String,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val accesibilidadAfectada: String,
    val gravedad: Gravedad,
    val esUrgente: Boolean,
    val esObstaculoTemporal: Boolean,
    val direccionTexto: String,
    val latitud: Double?,
    val longitud: Double?,
    val fotoUri: String?,
    val estado: EstadoIncidencia,
    val comentarioAdmin: String,
    val fechaEpochMs: Long
)
