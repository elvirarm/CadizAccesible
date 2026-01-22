package com.example.cadizaccesible.ui.screens.reports

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cadizaccesible.data.reports.Incidencia
import com.example.cadizaccesible.data.reports.RepositorioIncidencias

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBandejaAdmin(
    alAbrirDetalle: (String) -> Unit
) {
    LaunchedEffect(Unit) { RepositorioIncidencias.precargarDemoSiVacio() }

    val lista = remember { RepositorioIncidencias.obtenerTodas() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Bandeja de incidencias") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = lista,
                key = { it.id }
            ) { incidencia ->
                TarjetaAdminIncidencia(
                    incidencia = incidencia,
                    alPulsar = { alAbrirDetalle(incidencia.id) }
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
