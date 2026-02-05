package com.example.cadizaccesible.ui.screens.reports

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cadizaccesible.data.reports.EstadoIncidencia
import com.example.cadizaccesible.data.reports.RepositorioIncidenciasRoom
import com.example.cadizaccesible.ui.components.CampoTextoConVoz
import com.example.cadizaccesible.data.reports.Gravedad

/**
 * Pantalla integral de visualización y gestión de incidencias.
 * * Este componente es dual:
 * 1. Para el Ciudadano: Muestra el progreso de su reporte y los comentarios del técnico.
 * 2. Para el Administrador: Ofrece una consola de gestión para cambiar estados y responder.
 * * @param idIncidencia Identificador único de la incidencia a cargar.
 * @param esAdmin Determina si se muestran los controles de edición gubernamental.
 * @param alVolver Callback para navegar hacia atrás en el stack.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleIncidencia(
    idIncidencia: String,
    esAdmin: Boolean,
    alVolver: () -> Unit
) {
    // Inicialización de dependencias y ViewModel mediante Factory
    val contexto = LocalContext.current
    val repo = remember { RepositorioIncidenciasRoom(contexto) }

    val vm: DetalleIncidenciaViewModel = viewModel(
        factory = DetalleIncidenciaViewModel.Factory(repo, idIncidencia)
    )

    val state by vm.ui.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->

        // --- ESTADO: CARGANDO ---
        if (state.cargando) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(18.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Cargando incidencia…", style = MaterialTheme.typography.titleMedium)
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        OutlinedButton(onClick = alVolver, modifier = Modifier.fillMaxWidth()) {
                            Text("Volver")
                        }
                    }
                }
            }
            return@Scaffold
        }

        // --- ESTADO: ERROR O NO ENCONTRADO ---
        val incidencia = state.incidencia
        if (incidencia == null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(18.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = state.error.ifBlank { "No se encontró la incidencia." },
                            style = MaterialTheme.typography.titleMedium
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { vm.cargar() },
                                modifier = Modifier.weight(1f)
                            ) { Text("Reintentar") }

                            Button(
                                onClick = alVolver,
                                modifier = Modifier.weight(1f)
                            ) { Text("Volver") }
                        }
                    }
                }
            }
            return@Scaffold
        }

        // --- LÓGICA DE COLORES DINÁMICOS SEGÚN ESTADO ---
        val headerColor = when (incidencia.estado) {
            EstadoIncidencia.PENDIENTE -> MaterialTheme.colorScheme.tertiaryContainer
            EstadoIncidencia.EN_REVISION -> MaterialTheme.colorScheme.secondaryContainer
            EstadoIncidencia.ACEPTADA -> MaterialTheme.colorScheme.primaryContainer
            EstadoIncidencia.RESUELTA -> MaterialTheme.colorScheme.secondaryContainer
            EstadoIncidencia.RECHAZADA -> MaterialTheme.colorScheme.errorContainer
        }

        val headerOnColor = when (incidencia.estado) {
            EstadoIncidencia.PENDIENTE -> MaterialTheme.colorScheme.onTertiaryContainer
            EstadoIncidencia.EN_REVISION -> MaterialTheme.colorScheme.onSecondaryContainer
            EstadoIncidencia.ACEPTADA -> MaterialTheme.colorScheme.onPrimaryContainer
            EstadoIncidencia.RESUELTA -> MaterialTheme.colorScheme.onSecondaryContainer
            EstadoIncidencia.RECHAZADA -> MaterialTheme.colorScheme.onErrorContainer
        }

        val cardMod = Modifier
            .fillMaxWidth()
            .heightIn(min = 140.dp)

        // --- CONTENIDO PRINCIPAL (SCROLLABLE) ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(18.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            if (state.error.isNotBlank()) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = state.error,
                        modifier = Modifier.padding(14.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            /** Cabecera: Título, descripción y metadatos visuales */
            ElevatedCard(
                modifier = cardMod,
                colors = CardDefaults.elevatedCardColors(containerColor = headerColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = incidencia.titulo,
                        style = MaterialTheme.typography.headlineSmall,
                        color = headerOnColor
                    )
                    Text(
                        text = incidencia.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = headerOnColor.copy(alpha = 0.85f)
                    )

                    FlowRowChipsDetalle(
                        estado = incidencia.estado,
                        gravedad = incidencia.gravedad,
                        urgente = incidencia.esUrgente,
                        temporal = incidencia.esObstaculoTemporal
                    )
                }
            }

            /** Información de Clasificación y Categoría */
            ElevatedCard(
                modifier = cardMod,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Información", style = MaterialTheme.typography.titleMedium)
                    Text("Categoría: ${incidencia.categoria}")
                    Text("Accesibilidad: ${incidencia.accesibilidadAfectada}")
                    Text(
                        "Dirección: ${incidencia.direccionTexto.ifBlank { "Sin dirección" }}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            /** Sección Multimedia: Foto adjunta */
            if (!incidencia.fotoUri.isNullOrBlank()) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Foto", style = MaterialTheme.typography.titleMedium)
                        AsyncImage(
                            model = incidencia.fotoUri,
                            contentDescription = "Foto de la incidencia",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp)
                                .clip(MaterialTheme.shapes.large),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            /** Sección de Ubicación con integración de Google Maps */
            if (incidencia.latitud != null && incidencia.longitud != null) {
                ElevatedCard(
                    modifier = cardMod,
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Ubicación", style = MaterialTheme.typography.titleMedium)

                        Text(
                            text = "Dirección: ${incidencia.direccionTexto.ifBlank { "Sin dirección" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Button(
                            onClick = {
                                val query = if (incidencia.direccionTexto.isNotBlank()) {
                                    Uri.encode(incidencia.direccionTexto)
                                } else {
                                    Uri.encode("${incidencia.latitud},${incidencia.longitud}")
                                }
                                val uri = "https://www.google.com/maps/search/?api=1&query=$query".toUri()
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                contexto.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = incidencia.direccionTexto.isNotBlank() ||
                                    (incidencia.latitud != null && incidencia.longitud != null)
                        ) {
                            Text("Abrir en Google Maps")
                        }
                    }
                }
            }

            /** FLUJO CIUDADANO: Comentarios de la Administración */
            if (!esAdmin) {
                if (incidencia.comentarioAdmin.isNotBlank()) {
                    ElevatedCard(
                        modifier = cardMod,
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Comentario del admin", style = MaterialTheme.typography.titleMedium)
                            Text(
                                incidencia.comentarioAdmin,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                Button(
                    onClick = alVolver,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Volver") }

                return@Column
            }

            /** FLUJO ADMIN: Consola de Gestión Operativa */
            ElevatedCard(
                modifier = cardMod,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Gestión (Admin)", style = MaterialTheme.typography.titleMedium)

                    CampoTextoConVoz(
                        value = state.comentarioAdmin,
                        onValueChange = vm::onComentarioAdmin,
                        label = "Comentario del admin",
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Text("Cambiar estado", style = MaterialTheme.typography.bodyMedium)

                    // Matriz de botones de cambio de estado
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { vm.cambiarEstado(EstadoIncidencia.ACEPTADA) },
                            enabled = !state.actualizando,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) { Text("Aceptar") }

                        Button(
                            onClick = { vm.cambiarEstado(EstadoIncidencia.RECHAZADA) },
                            enabled = !state.actualizando,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) { Text("Rechazar") }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { vm.cambiarEstado(EstadoIncidencia.EN_REVISION) },
                            enabled = !state.actualizando,
                            modifier = Modifier.weight(1f)
                        ) { Text("En revisión") }

                        Button(
                            onClick = { vm.cambiarEstado(EstadoIncidencia.RESUELTA) },
                            enabled = !state.actualizando,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) { Text("Resuelta") }
                    }

                    if (state.actualizando) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            OutlinedButton(
                onClick = alVolver,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Volver") }
        }
    }
}

/** Organizador de indicadores visuales (Chips) en la cabecera */
@Composable
private fun FlowRowChipsDetalle(
    estado: EstadoIncidencia,
    gravedad: Gravedad,
    urgente: Boolean,
    temporal: Boolean
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ChipEstado(estado = estado)
        ChipGravedad(gravedad = gravedad)
        if (urgente) ChipEtiqueta(text = "URGENTE", kind = ChipEtiquetaKind.Danger)
        if (temporal) ChipEtiqueta(text = "Temporal", kind = ChipEtiquetaKind.Neutral)
    }
}

/** Chip especializado para mostrar el estado con colores semánticos */
@Composable
private fun ChipEstado(estado: EstadoIncidencia) {
    val (container, label) = when (estado) {
        EstadoIncidencia.PENDIENTE ->
            MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        EstadoIncidencia.EN_REVISION ->
            MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        EstadoIncidencia.ACEPTADA ->
            MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        EstadoIncidencia.RESUELTA ->
            MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        EstadoIncidencia.RECHAZADA ->
            MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }

    AssistChip(
        onClick = {},
        label = {
            Text(
                text = estado.textoChip(),
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = container,
            labelColor = label
        )
    )
}

/** Chip para visualizar la gravedad del reporte */
@Composable
private fun ChipGravedad(gravedad: Gravedad) {
    val (container, label) = when (gravedad) {
        Gravedad.BAJA ->
            MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        Gravedad.MEDIA ->
            MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        Gravedad.ALTA ->
            MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }

    AssistChip(
        onClick = {},
        label = {
            Text(
                text = gravedad.name,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = container,
            labelColor = label
        )
    )
}

private enum class ChipEtiquetaKind { Neutral, Danger }

/** Chip genérico para etiquetas informativas adicionales */
@Composable
private fun ChipEtiqueta(text: String, kind: ChipEtiquetaKind) {
    val (container, label) = when (kind) {
        ChipEtiquetaKind.Danger ->
            MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        ChipEtiquetaKind.Neutral ->
            MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
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
        )
    )
}

/** Función de extensión para compactar nombres de estado largos en la interfaz */
private fun EstadoIncidencia.textoChip(): String =
    when (this) {
        EstadoIncidencia.EN_REVISION -> "En rev."
        EstadoIncidencia.PENDIENTE -> "Pendiente"
        EstadoIncidencia.ACEPTADA -> "Aceptada"
        EstadoIncidencia.RESUELTA -> "Resuelta"
        EstadoIncidencia.RECHAZADA -> "Rechazada"
    }