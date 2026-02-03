package com.example.cadizaccesible.ui.screens.reports

import androidx.compose.foundation.background
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.example.cadizaccesible.data.reports.EstadoIncidencia
import com.example.cadizaccesible.data.reports.Incidencia
import com.example.cadizaccesible.data.reports.RepositorioIncidenciasRoom

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PantallaBandejaAdmin(
    alAbrirDetalle: (String) -> Unit
) {
    val contexto = LocalContext.current
    val repo = remember { RepositorioIncidenciasRoom(contexto) }
    val scope = rememberCoroutineScope()

    val lista by repo.obtenerTodas().collectAsState(initial = emptyList())

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = { TopAppBar(title = { Text("Bandeja de incidencias") }) }
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
                    Text("Gestión rápida", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Derecha: En revisión ·  Izquierda: Rechazada",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (lista.isEmpty()) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("No hay incidencias", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Cuando la ciudadanía reporte incidencias, aparecerán aquí.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(
                        start = 0.dp,
                        end = 0.dp,
                        top = 6.dp,
                        bottom = 14.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items = lista, key = { it.id }) { incidencia ->

                        val estadoSwipe = rememberDismissState()

                        LaunchedEffect(estadoSwipe.currentValue) {
                            when (estadoSwipe.currentValue) {
                                DismissValue.DismissedToEnd -> {
                                    scope.launch {
                                        repo.actualizarEstado(
                                            id = incidencia.id,
                                            nuevoEstado = EstadoIncidencia.EN_REVISION,
                                            comentarioAdmin = "Marcada en revisión desde swipe."
                                        )
                                        estadoSwipe.reset()
                                    }
                                }

                                DismissValue.DismissedToStart -> {
                                    scope.launch {
                                        repo.actualizarEstado(
                                            id = incidencia.id,
                                            nuevoEstado = EstadoIncidencia.RECHAZADA,
                                            comentarioAdmin = "Rechazada desde swipe."
                                        )
                                        estadoSwipe.reset()
                                    }
                                }

                                else -> Unit
                            }
                        }

                        SwipeToDismiss(
                            state = estadoSwipe,
                            directions = setOf(
                                DismissDirection.StartToEnd,
                                DismissDirection.EndToStart
                            ),
                            background = { FondoSwipeAdmin(estadoSwipe.dismissDirection) },
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
    }
}

@Composable
private fun FondoSwipeAdmin(direccion: DismissDirection?) {
    val config = when (direccion) {
        DismissDirection.StartToEnd -> FondoSwipeConfig(
            texto = "EN REVISIÓN",
            icono = Icons.Filled.Check,
            colorFondo = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
            colorContenido = MaterialTheme.colorScheme.onSurface,
            alineacion = Alignment.CenterStart
        )

        DismissDirection.EndToStart -> FondoSwipeConfig(
            texto = "RECHAZADA",
            icono = Icons.Filled.Close,
            colorFondo = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
            colorContenido = MaterialTheme.colorScheme.onSurface,
            alineacion = Alignment.CenterEnd
        )

        else -> FondoSwipeConfig(
            texto = "",
            icono = Icons.Filled.Check,
            colorFondo = Color.Transparent,
            colorContenido = MaterialTheme.colorScheme.onSurface,
            alineacion = Alignment.CenterStart
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(config.colorFondo)
            .padding(horizontal = 18.dp),
        contentAlignment = config.alineacion
    ) {
        if (direccion != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (direccion == DismissDirection.StartToEnd) {
                    Icon(
                        imageVector = config.icono,
                        contentDescription = config.texto,
                        tint = config.colorContenido
                    )
                    Text(
                        text = config.texto,
                        style = MaterialTheme.typography.titleMedium,
                        color = config.colorContenido
                    )
                } else {
                    Text(
                        text = config.texto,
                        style = MaterialTheme.typography.titleMedium,
                        color = config.colorContenido
                    )
                    Icon(
                        imageVector = config.icono,
                        contentDescription = config.texto,
                        tint = config.colorContenido
                    )
                }
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
        modifier = Modifier
            .fillMaxWidth()
            .clickable { alPulsar() },
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = incidencia.titulo,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Reportado por: ${incidencia.emailCreador}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                ChipTonal(text = incidencia.estado.name)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (incidencia.esUrgente) ChipEstado(text = "URGENTE", danger = true)
                if (incidencia.esObstaculoTemporal) ChipTonal(text = "Temporal")
            }
        }
    }
}


@Composable
private fun ChipTonal(text: String) {
    AssistChip(
        onClick = {},
        label = {
            Text(
                text = text,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = null
    )
}

@Composable
private fun ChipEstado(text: String, danger: Boolean) {
    val (container, label) = if (danger) {
        MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }

    AssistChip(
        onClick = {},
        label = {
            Text(
                text = text,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = container,
            labelColor = label
        ),
        border = null
    )
}

private data class FondoSwipeConfig(
    val texto: String,
    val icono: ImageVector,
    val colorFondo: Color,
    val colorContenido: Color,
    val alineacion: Alignment
)
