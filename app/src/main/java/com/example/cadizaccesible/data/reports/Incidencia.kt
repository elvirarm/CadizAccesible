package com.example.cadizaccesible.data.reports

data class Incidencia(
    val id: String,
    val emailCreador: String,

    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val accesibilidadAfectada: String,

    val gravedad: Gravedad,
    val esUrgente: Boolean,
    val esObstaculoTemporal: Boolean,

    val direccionTexto: String,
    val latitud: Double? = null,
    val longitud: Double? = null,

    val fotoUri: String? = null,

    val estado: EstadoIncidencia = EstadoIncidencia.PENDIENTE,
    val comentarioAdmin: String = "",

    val fechaEpochMs: Long = System.currentTimeMillis()
)
