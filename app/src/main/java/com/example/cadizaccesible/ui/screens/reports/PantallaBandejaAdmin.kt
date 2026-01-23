package com.example.cadizaccesible.ui.screens.reports

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cadizaccesible.data.reports.EstadoIncidencia
import com.example.cadizaccesible.data.reports.Incidencia
import com.example.cadizaccesible.data.reports.RepositorioIncidenciasRoom
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PantallaBandejaAdmin(
    alAbrirDetalle: (String) -> Unit
) {
    val contexto = LocalContext.current
    val repo = remember { RepositorioIncidenciasRoom(contexto) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        repo.precargarDemoSiVacio()
    }

    val lista by repo
        .obtenerTodas()
        .collectAsState(initial = emptyList())

    Scaffold(
        topBar = { TopAppBar(title = { Text("Bandeja de incidencias") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = lista,
                key = { it.id }
            ) { incidencia ->

                val estadoSwipe = rememberDismissState(
                    confirmStateChange = { valor ->
                        when (valor) {
                            DismissValue.DismissedToStart -> { // izquierda -> eliminar
                                scope.launch { repo.eliminarIncidencia(incidencia.id) }
                                true
                            }

                            DismissValue.DismissedToEnd -> { // derecha -> resuelta
                                scope.launch {
                                    repo.actualizarEstado(
                                        id = incidencia.id,
                                        nuevoEstado = EstadoIncidencia.RESUELTA,
                                        comentarioAdmin = "Marcada como resuelta desde swipe."
                                    )
                                }
                                true
                            }

                            else -> false
                        }
                    }
                )

                SwipeToDismiss(
                    state = estadoSwipe,
                    directions = setOf(
                        DismissDirection.EndToStart,  // izquierda
                        DismissDirection.StartToEnd   // derecha
                    ),
                    background = {},
                    dismissContent = {
                        TarjetaAdminIncidencia(
                            incidencia = incidencia,
                            alPulsar = { alAbrirDetalle(incidencia.id) }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun TarjetaAdminIncidencia(
    incidencia: Incidencia,
    alPulsar: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().clickable { alPulsar() }
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(incidencia.titulo, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text("De: ${incidencia.emailCreador}", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            AssistChip(onClick = {}, label = { Text(incidencia.estado.name) })
        }
    }
}
