package com.example.cadizaccesible.data.sesion

import com.example.cadizaccesible.data.users.RolUsuario

/**
 * Representa el estado actual de la sesión del usuario en la aplicación.
 * * Esta clase es inmutable y se utiliza para transportar la información de autenticación
 * desde la capa de datos (DataStore) hasta la capa de interfaz de usuario (UI).
 * * @property estaLogueado Indica si existe un usuario con sesión activa en el dispositivo.
 * @property email Correo electrónico del usuario autenticado; vacío si no hay sesión.
 * @property rol Nivel de acceso del usuario ([RolUsuario]), determina las funcionalidades disponibles.
 */
data class Sesion(
    val estaLogueado: Boolean = false,
    val email: String = "",
    val rol: RolUsuario = RolUsuario.CIUDADANO
)