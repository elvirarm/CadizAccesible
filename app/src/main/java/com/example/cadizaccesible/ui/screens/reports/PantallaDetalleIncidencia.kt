package com.example.cadizaccesible.ui.screens.reports

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cadizaccesible.data.reports.EstadoIncidencia
import com.example.cadizaccesible.data.reports.RepositorioIncidenciasRoom

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleIncidencia(
    idIncidencia: String,
    esAdmin: Boolean,
    alVolver: () -> Unit
) {
    val contexto = LocalContext.current

    val repo = remember { RepositorioIncidenciasRoom(contexto) }

    val vm: DetalleIncidenciaViewModel = viewModel(
        factory = DetalleIncidenciaViewModel.Factory(repo, idIncidencia)
    )

    val state by vm.ui.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Detalle de la incidencia") }) }
    ) { padding ->

        if (state.cargando) {
            Column(
                Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CircularProgressIndicator()
                Text("Cargando…")
                OutlinedButton(onClick = alVolver) { Text("Volver") }
            }
            return@Scaffold
        }

        val incidencia = state.incidencia
        if (incidencia == null) {
            Column(
                Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(state.error.ifBlank { "No se encontró la incidencia." })
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = { vm.cargar() }) { Text("Reintentar") }
                    Button(onClick = alVolver) { Text("Volver") }
                }
            }
            return@Scaffold
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            if (state.error.isNotBlank()) {
                AssistChip(onClick = {}, label = { Text(state.error) })
            }

            Text(incidencia.titulo, style = MaterialTheme.typography.headlineSmall)
            Text(incidencia.descripcion)

            Spacer(Modifier.height(6.dp))
            Text("Categoría: ${incidencia.categoria}")
            Text("Accesibilidad: ${incidencia.accesibilidadAfectada}")
            Text("Dirección: ${incidencia.direccionTexto}")

            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(incidencia.estado.name) })
                AssistChip(onClick = {}, label = { Text(incidencia.gravedad.name) })
                if (incidencia.esUrgente) AssistChip(onClick = {}, label = { Text("URGENTE") })
                if (incidencia.esObstaculoTemporal) AssistChip(onClick = {}, label = { Text("Temporal") })
            }

            if (!incidencia.fotoUri.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text("Foto", style = MaterialTheme.typography.titleMedium)
                AsyncImage(
                    model = incidencia.fotoUri,
                    contentDescription = "Foto de la incidencia",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop
                )
            }

            if (incidencia.latitud != null && incidencia.longitud != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Coordenadas: %.5f, %.5f".format(incidencia.latitud, incidencia.longitud),
                    style = MaterialTheme.typography.bodySmall
                )

                Button(
                    onClick = {
                        val uri = "https://www.google.com/maps/search/?api=1&query=${incidencia.latitud},${incidencia.longitud}".toUri()
                        contexto.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Abrir ubicación en Google Maps")
                }
            }

            Spacer(Modifier.height(10.dp))

            if (!esAdmin) {
                if (incidencia.comentarioAdmin.isNotBlank()) {
                    Text("Comentario del admin:", style = MaterialTheme.typography.titleMedium)
                    Text(incidencia.comentarioAdmin)
                }

                Spacer(Modifier.height(14.dp))
                Button(onClick = alVolver, modifier = Modifier.fillMaxWidth()) { Text("Volver") }
                return@Column
            }

            Text("Comentario del admin", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = state.comentarioAdmin,
                onValueChange = vm::onComentarioAdmin,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.actualizando
            )

            Spacer(Modifier.height(10.dp))
            Text("Acciones (Admin)", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { vm.cambiarEstado(EstadoIncidencia.ACEPTADA) },
                    enabled = !state.actualizando,
                    modifier = Modifier.weight(1f)
                ) { Text("Aceptar") }

                OutlinedButton(
                    onClick = { vm.cambiarEstado(EstadoIncidencia.RECHAZADA) },
                    enabled = !state.actualizando,
                    modifier = Modifier.weight(1f)
                ) { Text("Rechazar") }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { vm.cambiarEstado(EstadoIncidencia.EN_REVISION) },
                    enabled = !state.actualizando,
                    modifier = Modifier.weight(1f)
                ) { Text("En revisión") }

                Button(
                    onClick = { vm.cambiarEstado(EstadoIncidencia.RESUELTA) },
                    enabled = !state.actualizando,
                    modifier = Modifier.weight(1f)
                ) { Text("Resuelta") }
            }

            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = alVolver, modifier = Modifier.fillMaxWidth()) { Text("Volver") }
        }
    }
}
