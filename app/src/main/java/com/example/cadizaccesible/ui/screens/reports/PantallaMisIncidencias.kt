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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cadizaccesible.data.reports.Incidencia
import com.example.cadizaccesible.data.reports.RepositorioIncidenciasRoom
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PantallaMisIncidencias(
    emailUsuario: String,
    alAbrirDetalle: (String) -> Unit
) {
    val contexto = LocalContext.current
    val repo = remember { RepositorioIncidenciasRoom(contexto) }
    val scope = rememberCoroutineScope()



    val lista by repo
        .obtenerPorCreador(emailUsuario)
        .collectAsState(initial = emptyList())

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis incidencias") }) }
    ) { padding ->
        if (lista.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Aun no has creado incidencias.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = lista,
                    key = { it.id }
                ) { incidencia ->

                    val estadoSwipe = rememberDismissState(
                        confirmStateChange = { valor ->
                            if (valor == DismissValue.DismissedToStart) { // izquierda
                                scope.launch {
                                    repo.eliminarIncidencia(incidencia.id)
                                }
                                true
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismiss(
                        state = estadoSwipe,
                        directions = setOf(DismissDirection.EndToStart), // solo izquierda
                        background = {},
                        dismissContent = {
                            TarjetaIncidencia(
                                incidencia = incidencia,
                                alPulsar = { alAbrirDetalle(incidencia.id) }
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaIncidencia(
    incidencia: Incidencia,
    alPulsar: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().clickable { alPulsar() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(incidencia.titulo, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                Text("${incidencia.categoria} · ${incidencia.accesibilidadAfectada}")

                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = {}, label = { Text(incidencia.estado.name) })
                    if (incidencia.esUrgente) AssistChip(onClick = {}, label = { Text("URGENTE") })
                    if (incidencia.esObstaculoTemporal) AssistChip(onClick = {}, label = { Text("Temporal") })
                }

                Spacer(Modifier.height(8.dp))
                Text(incidencia.direccionTexto, style = MaterialTheme.typography.bodySmall)
            }

            if (!incidencia.fotoUri.isNullOrBlank()) {
                AsyncImage(
                    model = incidencia.fotoUri,
                    contentDescription = "Miniatura",
                    modifier = Modifier
                        .width(88.dp)
                        .height(88.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
