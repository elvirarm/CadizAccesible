package com.example.cadizaccesible.ui.navegation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.cadizaccesible.data.users.RolUsuario
import com.example.cadizaccesible.ui.screens.auth.PantallaLogin
import com.example.cadizaccesible.ui.screens.auth.PantallaRegistro
import com.example.cadizaccesible.ui.screens.auth.PantallaSplash
import com.example.cadizaccesible.ui.screens.home.PantallaInicioAdmin
import com.example.cadizaccesible.ui.screens.home.PantallaInicioCiudadano

@Composable
fun HostNavegacion(modifier: Modifier = Modifier) {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Rutas.Splash.ruta, modifier = modifier) {

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
                alLoguear = { rol: RolUsuario ->
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
            PantallaInicioCiudadano(
                alCerrarSesion = {
                    nav.navigate(Rutas.Login.ruta) {
                        popUpTo(Rutas.InicioCiudadano.ruta) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.InicioAdmin.ruta) {
            PantallaInicioAdmin(
                alCerrarSesion = {
                    nav.navigate(Rutas.Login.ruta) {
                        popUpTo(Rutas.InicioAdmin.ruta) { inclusive = true }
                    }
                }
            )
        }
    }
}
