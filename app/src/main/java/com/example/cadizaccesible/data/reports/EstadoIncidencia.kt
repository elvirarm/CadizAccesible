package com.example.cadizaccesible.data.reports

/**
 * Representa los posibles estados en los que se puede encontrar una incidencia reportada.
 * * Este Enum es fundamental para el flujo de trabajo de la aplicación, permitiendo
 * filtrar y gestionar el progreso de los reportes ciudadanos.
 */
enum class EstadoIncidencia {
    /** La incidencia ha sido enviada pero aún no ha sido procesada por los administradores. */
    PENDIENTE,

    /** La incidencia ha sido validada y está en cola para ser atendida. */
    ACEPTADA,

    /** Se está trabajando actualmente en la resolución o inspección de la incidencia. */
    EN_REVISION,

    /** El problema ha sido solucionado satisfactoriamente. */
    RESUELTA,

    /** La incidencia ha sido descartada (por ser duplicada, falsa o fuera de competencia). */
    RECHAZADA
}