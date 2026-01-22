package com.example.cadizaccesible.ui.navigation

sealed class Rutas(val ruta: String) {
    data object Splash : Rutas("splash")
    data object Login : Rutas("login")
    data object Registro : Rutas("registro")

    data object InicioCiudadano : Rutas("inicio_ciudadano")
    data object InicioAdmin : Rutas("inicio_admin")

    data object MisIncidencias : Rutas("mis_incidencias")
    data object BandejaIncidencias : Rutas("bandeja_incidencias")

    data object DetalleIncidencia : Rutas("detalle_incidencia/{id}") {
        fun crearRuta(id: String) = "detalle_incidencia/$id"
    }

    data object CrearIncidencia : Rutas("crear_incidencia")

}
