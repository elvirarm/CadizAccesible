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

/**
 * Pantalla de transición inicial (Splash Screen) encargada de la lógica de enrutamiento.
 * * Su propósito es doble:
 * 1. Proporcionar una bienvenida visual mientras se inicializan los recursos.
 * 2. Comprobar de forma asíncrona si existe una sesión activa y redirigir al flujo adecuado
 * (Login, Dashboard de Ciudadano o Dashboard de Administrador).
 * * @param irALogin Callback para navegar a la pantalla de autenticación.
 * @param irAInicioCiudadano Callback para navegar al inicio de usuarios estándar.
 * @param irAInicioAdmin Callback para navegar al inicio de usuarios con privilegios.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaSplash(
    irALogin: () -> Unit,
    irAInicioCiudadano: () -> Unit,
    irAInicioAdmin: () -> Unit
) {
    val contexto = LocalContext.current
    // Se instancia el gestor para consultar la persistencia en DataStore
    val gestorSesion = remember { GestorSesion(contexto) }

    /**
     * LaunchedEffect ejecuta la lógica de comprobación una sola vez al entrar en la composición.
     * Utiliza el flujo de sesión obteniendo solo el primer valor emitido ('first()').
     */
    LaunchedEffect(Unit) {
        val sesion = gestorSesion.flujoSesion.first()

        when {
            !sesion.estaLogueado -> irALogin()
            sesion.rol == RolUsuario.ADMIN -> irAInicioAdmin()
            else -> irAInicioCiudadano()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Cádiz Accesible",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Cargando tu sesión…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )

                    Spacer(Modifier.height(6.dp))

                    // Indicador de progreso que confirma al usuario que la app está trabajando
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}