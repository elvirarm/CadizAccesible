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
fun PantallaInicioAdmin(
    irABandeja: () -> Unit,
    irAInformes: () -> Unit,
    alCerrarSesion: () -> Unit
) {
    val contexto = LocalContext.current
    val gestorSesion = remember { GestorSesion(contexto) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text("Inicio", style = MaterialTheme.typography.headlineMedium)
        Text("Gestiona incidencias reportadas por la ciudadanía.")

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = irABandeja,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Bandeja de incidencias")
        }

        Button(
            onClick = irAInformes,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Informes y estadísticas")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                scope.launch {
                    gestorSesion.cerrarSesion()
                    alCerrarSesion()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}
