package com.example.cadizaccesible.data.reports

/**
 * Clase de datos que representa el resumen estadístico por nivel de gravedad.
 * * Esta clase se utiliza para mapear resultados de consultas de agregación (COUNT)
 * desde la base de datos, permitiendo cuantificar cuántas incidencias pertenecen
 * a cada nivel de severidad definido.
 * * @property gravedad El nivel de importancia de la incidencia (ej. BAJA, MEDIA, ALTA).
 * @property total La cantidad de registros encontrados para esa categoría de gravedad.
 */
data class ConteoGravedad(
    val gravedad: Gravedad,
    val total: Int
)