package com.example.cadizaccesible.ui.screens.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cadizaccesible.data.reports.RepositorioIncidenciasRoom
import com.example.cadizaccesible.ui.components.TarjetaIncidencia
import kotlinx.coroutines.launch

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete

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
        topBar = {
            TopAppBar(
                title = { Text("Mis incidencias") }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        text = "Tus reportes",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Desliza a la izquierda para eliminar una incidencia.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (lista.isEmpty()) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Aún no has creado incidencias",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Cuando crees una, aparecerá aquí para que puedas consultarla.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = lista,
                        key = { it.id }
                    ) { incidencia ->

                        val estadoSwipe = rememberDismissState(
                            confirmStateChange = { valor ->
                                if (valor == DismissValue.DismissedToStart) {
                                    scope.launch { repo.eliminarIncidencia(incidencia.id) }
                                    true
                                } else false
                            }
                        )

                        SwipeToDismiss(
                            state = estadoSwipe,
                            directions = setOf(DismissDirection.EndToStart),
                            background = {
                                FondoSwipeEliminar(direccion = estadoSwipe.dismissDirection)
                            },
                            dismissContent = {
                                TarjetaIncidencia(
                                    incidencia = incidencia,
                                    onClick = { id -> alAbrirDetalle(id) }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FondoSwipeEliminar(direccion: DismissDirection?) {
    val mostrando = direccion == DismissDirection.EndToStart

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (mostrando) MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                else Color.Transparent
            )
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        if (mostrando) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("ELIMINAR", style = MaterialTheme.typography.titleMedium)
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
            }
        }
    }
}
