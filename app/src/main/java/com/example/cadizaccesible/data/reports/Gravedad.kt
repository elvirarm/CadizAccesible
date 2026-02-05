package com.example.cadizaccesible.data.reports

/**
 * Define los niveles de severidad o urgencia de una incidencia.
 * * Esta clasificación permite priorizar la atención de los reportes en función
 * del impacto que tienen sobre la accesibilidad en la ciudad de Cádiz.
 */
enum class Gravedad {
    /** * Incidencias con impacto menor o estético.
     * No impiden el tránsito, pero requieren atención futura.
     */
    BAJA,

    /** * Incidencias que dificultan el acceso o presentan un obstáculo notable.
     * Requieren atención en un plazo de tiempo moderado.
     */
    MEDIA,

    /** * Incidencias críticas que impiden totalmente el acceso o suponen un peligro.
     * Deben ser tratadas con la máxima prioridad.
     */
    ALTA
}