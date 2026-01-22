package com.example.cadizaccesible.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cadizaccesible.data.sesion.GestorSesion
import com.example.cadizaccesible.data.users.RolUsuario
import com.example.cadizaccesible.ui.screens.auth.PantallaLogin
import com.example.cadizaccesible.ui.screens.auth.PantallaRegistro
import com.example.cadizaccesible.ui.screens.auth.PantallaSplash
import com.example.cadizaccesible.ui.screens.home.PantallaInicioAdmin
import com.example.cadizaccesible.ui.screens.home.PantallaInicioSesion
import com.example.cadizaccesible.ui.screens.reports.PantallaBandejaAdmin
import com.example.cadizaccesible.ui.screens.reports.PantallaCrearIncidencia
import com.example.cadizaccesible.ui.screens.reports.PantallaDetalleIncidencia
import com.example.cadizaccesible.ui.screens.reports.PantallaMisIncidencias

@Composable
fun HostNavegacion(modifier: Modifier = Modifier) {
    val nav = rememberNavController()

    val contexto = LocalContext.current
    val gestorSesion = remember { GestorSesion(contexto) }

    val sesion by gestorSesion.flujoSesion.collectAsState(initial = null)

    NavHost(
        navController = nav,
        startDestination = Rutas.Splash.ruta,
        modifier = modifier
    ) {

        composable(Rutas.Splash.ruta) {
            PantallaSplash(
                irALogin = {
                    nav.navigate(Rutas.Login.ruta) {
                        popUpTo(Rutas.Splash.ruta) { inclusive = true }
                    }
                },
                irAInicioCiudadano = {
                    nav.navigate(Rutas.InicioCiudadano.ruta) {
                        popUpTo(Rutas.Splash.ruta) { inclusive = true }
                    }
                },
                irAInicioAdmin = {
                    nav.navigate(Rutas.InicioAdmin.ruta) {
                        popUpTo(Rutas.Splash.ruta) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.Login.ruta) {
            PantallaLogin(
                irARegistro = { nav.navigate(Rutas.Registro.ruta) },
                alLoguear = { rol ->
                    if (rol == RolUsuario.ADMIN) {
                        nav.navigate(Rutas.InicioAdmin.ruta) {
                            popUpTo(Rutas.Login.ruta) { inclusive = true }
                        }
                    } else {
                        nav.navigate(Rutas.InicioCiudadano.ruta) {
                            popUpTo(Rutas.Login.ruta) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Rutas.Registro.ruta) {
            PantallaRegistro(
                volverALogin = { nav.popBackStack() }
            )
        }

        composable(Rutas.InicioCiudadano.ruta) {
            PantallaInicioSesion(
                irACrearIncidencia = {
                    nav.navigate(Rutas.CrearIncidencia.ruta)
                },
                irAMisIncidencias = {
                    nav.navigate(Rutas.MisIncidencias.ruta)
                },
                alCerrarSesion = {
                    nav.navigate(Rutas.Login.ruta) {
                        popUpTo(Rutas.InicioCiudadano.ruta) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.InicioAdmin.ruta) {
            PantallaInicioAdmin(
                irABandeja = {
                    nav.navigate(Rutas.BandejaIncidencias.ruta)
                },
                alCerrarSesion = {
                    nav.navigate(Rutas.Login.ruta) {
                        popUpTo(Rutas.InicioAdmin.ruta) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.MisIncidencias.ruta) {
            if (sesion == null) {
                PantallaCargandoSimple()
            } else {
                PantallaMisIncidencias(
                    emailUsuario = sesion!!.email,
                    alAbrirDetalle = { id ->
                        nav.navigate(Rutas.DetalleIncidencia.crearRuta(id))
                    }
                )
            }
        }

        composable(Rutas.BandejaIncidencias.ruta) {
            PantallaBandejaAdmin(
                alAbrirDetalle = { id ->
                    nav.navigate(Rutas.DetalleIncidencia.crearRuta(id))
                }
            )
        }

        composable(Rutas.DetalleIncidencia.ruta) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""

            if (sesion == null) {
                PantallaCargandoSimple()
            } else {
                PantallaDetalleIncidencia(
                    idIncidencia = id,
                    esAdmin = sesion!!.rol == RolUsuario.ADMIN,
                    alVolver = { nav.popBackStack() }
                )
            }
        }

        composable(Rutas.CrearIncidencia.ruta) {
            if (sesion == null) {
                PantallaCargandoSimple()
            } else {
                PantallaCrearIncidencia(
                    emailUsuario = sesion!!.email,
                    alCrear = { nav.popBackStack() },
                    alCancelar = { nav.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun PantallaCargandoSimple() {
    androidx.compose.material3.Scaffold { padding ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.material3.CircularProgressIndicator()
        }
    }
}
