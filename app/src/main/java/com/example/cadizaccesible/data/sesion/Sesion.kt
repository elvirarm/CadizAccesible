package com.example.cadizaccesible.data.sesion

import com.example.cadizaccesible.data.users.RolUsuario

data class Sesion(
    val estaLogueado: Boolean = false,
    val email: String = "",
    val rol: RolUsuario = RolUsuario.CIUDADANO
)
