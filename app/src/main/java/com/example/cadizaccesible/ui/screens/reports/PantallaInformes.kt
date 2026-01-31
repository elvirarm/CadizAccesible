package com.example.cadizaccesible.ui.screens.reports

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.cadizaccesible.data.reports.*
import androidx.compose.foundation.layout.FlowRow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInformes(
    alVolver: () -> Unit
) {
    val context = LocalContext.current
    val repo = remember { RepositorioIncidenciasRoom(context) }

    val vm: InformesViewModel = viewModel(
        factory = InformesViewModel.Factory(repo)
    )

    val total by vm.totalIncidencias.collectAsState()
    val urgentes by vm.totalUrgentes.collectAsState()
    val distEstados by vm.distEstados.collectAsState()
    val distGravedades by vm.distGravedades.collectAsState()
    val resumenFiltrado by vm.resumenFiltrado.collectAsState()
    val ui by vm.ui.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Informes") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // --- RESUMEN (RA5.b / RA5.d) ---
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ResumenCard("Total incidencias", total.toString(), Modifier.weight(1f))
                ResumenCard("Urgentes", urgentes.toString(), Modifier.weight(1f))
            }

            val porcentajeUrgentes = if (total == 0) 0 else (urgentes * 100 / total)
            Text("Urgentes: $porcentajeUrgentes%", style = MaterialTheme.typography.bodyMedium)

            Divider()

            // --- FILTROS (RA5.c) ---
            Text("Filtro por estado", style = MaterialTheme.typography.titleMedium)
            FiltroEstado(
                seleccionado = ui.filtroEstado,
                onSelected = { vm.setFiltroEstado(it); vm.setFiltroGravedad(null) }
            )

            Text("Filtro por gravedad", style = MaterialTheme.typography.titleMedium)
            FiltroGravedad(
                seleccionado = ui.filtroGravedad,
                onSelected = { vm.setFiltroGravedad(it); vm.setFiltroEstado(null) }
            )

            ResumenCard(
                titulo = "Resultado del filtro",
                valor = resumenFiltrado.toString(),
                modifier = Modifier.fillMaxWidth()
            )

            Divider()

            // --- GRAFICOS (RA5.e) ---
            Text("Incidencias por estado", style = MaterialTheme.typography.titleMedium)
            GraficoBarras(
                etiquetas = EstadoIncidencia.values().map { it.name },
                valores = EstadoIncidencia.values().map { e ->
                    distEstados.firstOrNull { it.estado == e }?.total ?: 0
                }
            )

            Text("Incidencias por gravedad", style = MaterialTheme.typography.titleMedium)
            GraficoBarras(
                etiquetas = Gravedad.values().map { it.name },
                valores = Gravedad.values().map { g ->
                    distGravedades.firstOrNull { it.gravedad == g }?.total ?: 0
                }
            )

            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = alVolver, modifier = Modifier.fillMaxWidth()) {
                Text("Volver")
            }
        }
    }
}

@Composable
private fun ResumenCard(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            Text(valor, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
private fun FiltroEstado(
    seleccionado: EstadoIncidencia?,
    onSelected: (EstadoIncidencia?) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        FilterChip(
            selected = seleccionado == null,
            onClick = { onSelected(null) },
            label = { Text("Todos") }
        )

        EstadoIncidencia.values().forEach { estado ->
            FilterChip(
                selected = seleccionado == estado,
                onClick = { onSelected(estado) },
                label = {
                    Text(
                        text = estado.textoUI(),
                        maxLines = 1,
                        softWrap = false
                    )
                }
            )
        }
    }
}


@Composable
private fun FiltroGravedad(
    seleccionado: Gravedad?,
    onSelected: (Gravedad?) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        FilterChip(
            selected = seleccionado == null,
            onClick = { onSelected(null) },
            label = { Text("Todas") }
        )

        Gravedad.values().forEach { g ->
            FilterChip(
                selected = seleccionado == g,
                onClick = { onSelected(g) },
                label = { Text(g.name) }
            )
        }
    }
}




@Composable
private fun GraficoBarras(
    etiquetas: List<String>,
    valores: List<Int>,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val max = (valores.maxOrNull() ?: 0).coerceAtLeast(1)

    val barColor = MaterialTheme.colorScheme.primary
    val axisColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)

    Card(modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // GRÁFICO
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                val w = size.width
                val h = size.height

                val leftPad = 8f
                val rightPad = 8f
                val topPad = 8f
                val bottomPad = 24f   // espacio para etiquetas

                val chartW = w - leftPad - rightPad
                val chartH = h - topPad - bottomPad
                val yBase = topPad + chartH

                // Eje X
                drawLine(
                    color = axisColor,
                    start = Offset(leftPad, yBase),
                    end = Offset(leftPad + chartW, yBase),
                    strokeWidth = 2f
                )

                fun yFor(v: Int): Float {
                    val ratio = v.toFloat() / max.toFloat()
                    return yBase - (ratio * chartH)
                }

                // Líneas guía
                listOf(0, max / 2, max).distinct().forEach { m ->
                    val y = yFor(m)
                    drawLine(
                        color = axisColor,
                        start = Offset(leftPad, y),
                        end = Offset(leftPad + chartW, y),
                        strokeWidth = 1f
                    )
                }

                // Barras
                val n = valores.size.coerceAtLeast(1)
                val gap = 6f
                val barW = ((chartW - gap * (n + 1)) / n).coerceAtLeast(10f)

                valores.forEachIndexed { i, v ->
                    val x = leftPad + gap + i * (barW + gap)
                    val barH = (v.toFloat() / max.toFloat()) * chartH

                    drawRect(
                        color = barColor,
                        topLeft = Offset(x, yBase - barH),
                        size = androidx.compose.ui.geometry.Size(barW, barH)
                    )
                }
            }

            // --- ETIQUETAS BAJO CADA BARRA ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                etiquetas.forEach { label ->
                    Text(
                        text = label.replace("_", "\n"), // EN_REVISION → EN\nREVISION
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.widthIn(min = 40.dp)
                    )
                }
            }

            // --- VALORES NUMÉRICOS (opcional pero recomendable) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                valores.forEach { v ->
                    Text(
                        text = v.toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

private fun EstadoIncidencia.textoUI(): String =
    when (this) {
        EstadoIncidencia.PENDIENTE -> "Pendiente"
        EstadoIncidencia.ACEPTADA -> "Aceptada"
        EstadoIncidencia.EN_REVISION -> "En revisión"
        EstadoIncidencia.RESUELTA -> "Resuelta"
        EstadoIncidencia.RECHAZADA -> "Rechazada"
    }

