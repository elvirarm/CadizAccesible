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
fun PantallaInicioAdmin(alCerrarSesion: () -> Unit) {
    val contexto = LocalContext.current
    val gestorSesion = remember { GestorSesion(contexto) }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Inicio (Admin)", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        Text("Aquí irá: Bandeja de pendientes, aceptar/rechazar, marcar resuelto, estadísticas.")

        Spacer(Modifier.height(20.dp))
        Button(onClick = {
            scope.launch { gestorSesion.cerrarSesion(); alCerrarSesion() }
        }) {
            Text("Cerrar sesión")
        }
    }
}
