package com.example.cadizaccesible.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cadizaccesible.data.sesion.GestorSesion
import com.example.cadizaccesible.data.users.RolUsuario
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun PantallaSplash(
    irALogin: () -> Unit,
    irAInicioCiudadano: () -> Unit,
    irAInicioAdmin: () -> Unit
) {
    val contexto = LocalContext.current
    val gestorSesion = remember { GestorSesion(contexto) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            val sesion = gestorSesion.flujoSesion.first()
            if (!sesion.estaLogueado) irALogin()
            else if (sesion.rol == RolUsuario.ADMIN) irAInicioAdmin()
            else irAInicioCiudadano()
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Cádiz Accesible", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(12.dp))
            CircularProgressIndicator()
        }
    }
}
