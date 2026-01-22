package com.example.cadizaccesible.ui.screens.reports

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cadizaccesible.data.reports.EstadoIncidencia
import com.example.cadizaccesible.data.reports.RepositorioIncidencias

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleIncidencia(
    idIncidencia: String,
    esAdmin: Boolean,
    alVolver: () -> Unit
) {
    val incidencia = remember(idIncidencia) {
        RepositorioIncidencias.obtenerPorId(idIncidencia)
    }

    if (incidencia == null) {
        Scaffold(topBar = { TopAppBar(title = { Text("Detalle") }) }) { padding ->
            Column(Modifier.padding(padding).padding(16.dp)) {
                Text("No se encontro la incidencia.")
                Spacer(Modifier.height(12.dp))
                Button(onClick = alVolver) { Text("Volver") }
            }
        }
        return
    }

    var comentarioAdmin by remember { mutableStateOf(incidencia.comentarioAdmin) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Detalle de la incidencia") }) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {

            Text(incidencia.titulo, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text(incidencia.descripcion)

            Spacer(Modifier.height(12.dp))
            Text("Categoria: ${incidencia.categoria}")
            Text("Accesibilidad: ${incidencia.accesibilidadAfectada}")
            Text("Direccion: ${incidencia.direccionTexto}")

            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(incidencia.estado.name) })
                if (incidencia.esUrgente) AssistChip(onClick = {}, label = { Text("URGENTE") })
                if (incidencia.esObstaculoTemporal) AssistChip(onClick = {}, label = { Text("Temporal") })
            }

            if (!esAdmin) {
                if (incidencia.comentarioAdmin.isNotBlank()) {
                    Spacer(Modifier.height(16.dp))
                    Text("Comentario del admin:", style = MaterialTheme.typography.titleMedium)
                    Text(incidencia.comentarioAdmin)
                }

                Spacer(Modifier.height(20.dp))
                Button(onClick = alVolver) { Text("Volver") }
                return@Column
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = comentarioAdmin,
                onValueChange = { comentarioAdmin = it },
                label = { Text("Comentario del admin") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))
            Text("Acciones (Admin):", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    RepositorioIncidencias.actualizarEstado(
                        idIncidencia,
                        EstadoIncidencia.ACEPTADA,
                        comentarioAdmin
                    )
                    alVolver()
                }) { Text("Aceptar") }

                OutlinedButton(onClick = {
                    RepositorioIncidencias.actualizarEstado(
                        idIncidencia,
                        EstadoIncidencia.RECHAZADA,
                        comentarioAdmin
                    )
                    alVolver()
                }) { Text("Rechazar") }
            }

            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    RepositorioIncidencias.actualizarEstado(
                        idIncidencia,
                        EstadoIncidencia.EN_REVISION,
                        comentarioAdmin
                    )
                    alVolver()
                }) { Text("En revision") }

                Button(onClick = {
                    RepositorioIncidencias.actualizarEstado(
                        idIncidencia,
                        EstadoIncidencia.RESUELTA,
                        comentarioAdmin
                    )
                    alVolver()
                }) { Text("Resuelta") }
            }

            Spacer(Modifier.height(20.dp))
            OutlinedButton(onClick = alVolver) { Text("Volver") }
        }
    }
}
