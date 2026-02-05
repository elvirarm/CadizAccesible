package com.example.cadizaccesible.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cadizaccesible.data.reports.EstadoIncidencia
import com.example.cadizaccesible.data.reports.Gravedad

/**
 * Representa una incidencia en la base de datos.
 *
 * @property id Identificador único de la incidencia.
 * @property emailCreador Email del usuario que ha creado la incidencia.
 * @property titulo Título de la incidencia.
 * @property descripcion Descripción detallada de la incidencia.
 * @property categoria Categoría a la que pertenece la incidencia.
 * @property accesibilidadAfectada Tipo de accesibilidad que se ve afectada.
 * @property gravedad Nivel de gravedad de la incidencia.
 * @property esUrgente Indica si la incidencia es urgente.
 * @property esObstaculoTemporal Indica si el obstáculo es temporal.
 * @property direccionTexto Dirección en formato de texto de la incidencia.
 * @property latitud Latitud de la ubicación de la incidencia.
 * @property longitud Longitud de la ubicación de la incidencia.
 * @property fotoUri URI de la foto asociada a la incidencia.
 * @property estado Estado actual de la incidencia.
 * @property comentarioAdmin Comentario del administrador sobre la incidencia.
 * @property fechaEpochMs Fecha de creación de la incidencia en milisegundos desde la época.
 */
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
