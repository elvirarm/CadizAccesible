package com.example.cadizaccesible.data.reports

/**
 * Clase de datos que representa un par de valores para estadísticas de incidencias.
 * * Se utiliza principalmente para almacenar el resultado de consultas de agregación en la base de datos,
 * permitiendo conocer cuántas incidencias existen para un estado específico.
 * * @property estado El tipo de estado de la incidencia (ej. PENDIENTE, EN_REPARACION, RESUELTA).
 * @property total La cantidad numérica de incidencias encontradas en dicho estado.
 */
data class ConteoEstado(
    val estado: EstadoIncidencia,
    val total: Int
)