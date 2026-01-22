package com.example.cadizaccesible.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cadizaccesible.data.sesion.GestorSesion
import kotlinx.coroutines.launch

@Composable
fun PantallaInicioCiudadano(
    irAMisIncidencias: () -> Unit,
    alCerrarSesion: () -> Unit
) {
    val contexto = LocalContext.current
    val gestorSesion = remember { GestorSesion(contexto) }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Inicio (Ciudadano)", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        Text("Accede a tus incidencias y ayuda a mejorar la accesibilidad en Cadiz.")

        Spacer(Modifier.height(20.dp))

        Button(onClick = irAMisIncidencias, modifier = Modifier.fillMaxWidth()) {
            Text("Mis incidencias")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                scope.launch { gestorSesion.cerrarSesion(); alCerrarSesion() }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesion")
        }
    }
}
