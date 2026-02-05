package com.example.cadizaccesible.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cadizaccesible.data.reports.Incidencia

/**
 * Componente visual diseñado para mostrar un resumen de una [Incidencia] en una lista.
 * * La tarjeta organiza la información para que los detalles críticos (título, estado, urgencia)
 * sean visibles de un vistazo. Incluye soporte para imágenes remotas o locales mediante Coil.
 * * @param incidencia El objeto de datos con la información del reporte.
 * @param onClick Callback que se dispara al pulsar la tarjeta, devolviendo el ID de la incidencia.
 * @param modifier [Modifier] para ajustar el diseño exterior de la tarjeta.
 * @param mostrarMiniatura Si es true y existe una URI de foto, muestra una imagen cuadrada a la derecha.
 */
@Composable
fun TarjetaIncidencia(
    incidencia: Incidencia,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    mostrarMiniatura: Boolean = true
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(incidencia.id) },
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Columna principal con la información textual
            Column(Modifier.weight(1f)) {
                Text(
                    text = incidencia.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "${incidencia.categoria} · ${incidencia.accesibilidadAfectada}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(10.dp))

                // Fila de indicadores (Chips) de estado y prioridad
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatusChip(
                        text = incidencia.estado.name,
                        kind = when {
                            // Lógica semántica para colorear el estado
                            incidencia.estado.name.contains("RESUEL", ignoreCase = true) -> StatusKind.Success
                            incidencia.estado.name.contains("PEND", ignoreCase = true) -> StatusKind.Warning
                            else -> StatusKind.Neutral
                        }
                    )

                    if (incidencia.esUrgente) {
                        StatusChip(text = "URGENTE", kind = StatusKind.Danger)
                    }

                    if (incidencia.esObstaculoTemporal) {
                        TagChip(text = "Temporal")
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Ubicación de la incidencia
                Text(
                    text = incidencia.direccionTexto.ifBlank { "Sin dirección" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Imagen lateral (Miniatura)
            if (mostrarMiniatura && !incidencia.fotoUri.isNullOrBlank()) {
                AsyncImage(
                    model = incidencia.fotoUri,
                    contentDescription = "Miniatura de la incidencia",
                    modifier = Modifier
                        .size(92.dp),
                    contentScale = ContentScale.Crop // Recorta la imagen para llenar el cuadrado
                )
            }
        }
    }
}