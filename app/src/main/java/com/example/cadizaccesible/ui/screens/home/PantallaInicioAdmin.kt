package com.example.cadizaccesible.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cadizaccesible.data.sesion.GestorSesion
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import com.example.cadizaccesible.ui.theme.AppThemeState
import com.example.cadizaccesible.data.sesion.ThemePrefs
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicioAdmin(
    irABandeja: () -> Unit,
    irAInformes: () -> Unit,
    alCerrarSesion: () -> Unit
) {
    val contexto = LocalContext.current
    val gestorSesion = remember { GestorSesion(contexto) }
    val scope = rememberCoroutineScope()
    val prefs = remember { ThemePrefs(contexto) }


    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Administración") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(18.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Panel de administración",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Gestiona y analiza las incidencias reportadas por la ciudadanía.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                }
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Gestión", style = MaterialTheme.typography.titleMedium)

                    Button(
                        onClick = irABandeja,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Bandeja de incidencias") }

                    OutlinedButton(
                        onClick = irAInformes,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Informes y estadísticas") }
                }
            }

            // ✅ Switch modo oscuro
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Modo oscuro", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Cambiar apariencia de la aplicación",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = AppThemeState.darkMode.value,
                        onCheckedChange = { enabled ->
                            AppThemeState.darkMode.value = enabled
                            scope.launch { prefs.setDarkMode(enabled) }
                        }                    )
                }
            }

            Spacer(Modifier.weight(1f))

            OutlinedButton(
                onClick = {
                    scope.launch {
                        gestorSesion.cerrarSesion()
                        alCerrarSesion()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) { Text("Cerrar sesión") }
        }
    }
}
