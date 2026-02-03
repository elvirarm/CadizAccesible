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

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatusChip(
                        text = incidencia.estado.name,
                        kind = when {
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

                Text(
                    text = incidencia.direccionTexto.ifBlank { "Sin dirección" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (mostrarMiniatura && !incidencia.fotoUri.isNullOrBlank()) {
                AsyncImage(
                    model = incidencia.fotoUri,
                    contentDescription = "Miniatura",
                    modifier = Modifier
                        .size(92.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
