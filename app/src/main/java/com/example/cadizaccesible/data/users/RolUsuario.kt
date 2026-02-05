package com.example.cadizaccesible.data.users

/**
 * Define los roles de acceso disponibles en el sistema "Cádiz Accesible".
 * * Este enumerado se utiliza para la lógica de autorización, determinando qué
 * funcionalidades de la interfaz de usuario y qué operaciones de datos
 * están permitidas para el usuario actual.
 */
enum class RolUsuario {
    /**
     * Usuario estándar orientado al ciudadano.
     * Permisos: Crear incidencias, consultar sus propios reportes y ver el mapa público.
     */
    CIUDADANO,

    /**
     * Usuario con privilegios de gestión.
     * Permisos: Ver todas las incidencias, cambiar el estado de los reportes y
     * añadir comentarios administrativos de resolución.
     */
    ADMIN
}