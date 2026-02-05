package com.example.cadizaccesible.data.reports

/**
 * Modelo de datos representativo de una incidencia de accesibilidad en la ciudad.
 * * Contiene toda la información necesaria sobre el problema reportado, su ubicación,
 * estado de gestión y detalles técnicos para su visualización y filtrado.
 * * @property id Identificador único de la incidencia.
 * @property emailCreador Correo electrónico del usuario que registró el reporte.
 * @property titulo Título breve y descriptivo del problema.
 * @property descripcion Explicación detallada de la incidencia encontrada.
 * @property categoria Clasificación del tipo de incidencia (ej. Aceras, Semáforos, Rampas).
 * @property accesibilidadAfectada Descripción del tipo de limitación que genera (visual, motora, etc.).
 * @property gravedad Nivel de prioridad asignado ([Gravedad]).
 * @property esUrgente Flag que indica si la incidencia requiere intervención inmediata.
 * @property esObstaculoTemporal Indica si el problema es puntual (ej. un vehículo mal estacionado) o permanente.
 * @property direccionTexto Dirección postal o descriptiva del lugar de los hechos.
 * @property latitud Coordenada de latitud para posicionamiento en mapa (opcional).
 * @property longitud Coordenada de longitud para posicionamiento en mapa (opcional).
 * @property fotoUri Ruta o URI de la imagen adjunta como evidencia visual.
 * @property estado Estado actual en el ciclo de vida del reporte ([EstadoIncidencia]).
 * @property comentarioAdmin Notas o respuesta proporcionada por los técnicos municipales.
 * @property fechaEpochMs Marca de tiempo en milisegundos de cuándo se creó el reporte.
 */
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