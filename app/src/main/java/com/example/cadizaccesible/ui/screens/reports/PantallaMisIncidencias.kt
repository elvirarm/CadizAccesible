package com.example.cadizaccesible.ui.screens.reports

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cadizaccesible.data.reports.Incidencia
import com.example.cadizaccesible.data.reports.RepositorioIncidencias

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMisIncidencias(
    emailUsuario: String,
    alAbrirDetalle: (String) -> Unit
) {
    LaunchedEffect(Unit) { RepositorioIncidencias.precargarDemoSiVacio() }

    val lista = remember(emailUsuario) {
        RepositorioIncidencias.obtenerPorCreador(emailUsuario)
    }

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
                items(lista, key = { it.id }) { incidencia ->
                    TarjetaIncidencia(
                        incidencia = incidencia,
                        alPulsar = { alAbrirDetalle(incidencia.id) }
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
        Column(Modifier.padding(12.dp)) {
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
    }
}
