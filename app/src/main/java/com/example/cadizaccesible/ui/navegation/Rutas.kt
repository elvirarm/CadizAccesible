package com.example.cadizaccesible.ui.navegation

sealed class Rutas(val ruta: String) {
    data object Splash : Rutas("splash")
    data object Login : Rutas("login")
    data object Registro : Rutas("registro")
    data object InicioCiudadano : Rutas("inicio_ciudadano")
    data object InicioAdmin : Rutas("inicio_admin")
}