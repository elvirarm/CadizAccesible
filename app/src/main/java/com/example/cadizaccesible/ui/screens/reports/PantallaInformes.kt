package com.example.cadizaccesible.ui.screens.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cadizaccesible.data.reports.*
import com.example.cadizaccesible.ui.components.GraficoBarras

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

    val porcentajeUrgentes = if (total == 0) 0 else (urgentes * 100 / total)

    val kpiTotalBg = MaterialTheme.colorScheme.secondaryContainer
    val kpiUrgBg = MaterialTheme.colorScheme.tertiaryContainer
    val filtrosBg = MaterialTheme.colorScheme.surfaceVariant
    val graficoBg = MaterialTheme.colorScheme.surfaceVariant

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Informes") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(14.dp)
                .verticalScroll(rememberScrollState()),
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
                    Text("Panel de estadísticas", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Resumen de incidencias y distribución por estado/gravedad.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResumenCardPro(
                    titulo = "Total",
                    valor = total.toString(),
                    subtitulo = "Incidencias registradas",
                    modifier = Modifier.weight(1f),
                    container = kpiTotalBg
                )
                ResumenCardPro(
                    titulo = "Urgentes",
                    valor = urgentes.toString(),
                    subtitulo = "$porcentajeUrgentes% del total",
                    modifier = Modifier.weight(1f),
                    container = kpiUrgBg
                )
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = filtrosBg
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Filtros", style = MaterialTheme.typography.titleMedium)

                    Text("Por estado", style = MaterialTheme.typography.bodyMedium)
                    FiltroEstadoPro(
                        seleccionado = ui.filtroEstado,
                        onSelected = { vm.setFiltroEstado(it); vm.setFiltroGravedad(null) }
                    )

                    Text("Por gravedad", style = MaterialTheme.typography.bodyMedium)
                    FiltroGravedadPro(
                        seleccionado = ui.filtroGravedad,
                        onSelected = { vm.setFiltroGravedad(it); vm.setFiltroEstado(null) }
                    )

                    Spacer(Modifier.height(2.dp))

                    ResumenCardPro(
                        titulo = "Resultado del filtro",
                        valor = resumenFiltrado.toString(),
                        subtitulo = "Incidencias que cumplen el filtro",
                        modifier = Modifier.fillMaxWidth(),
                        container = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = graficoBg
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Incidencias por estado", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Distribución del estado de tramitación.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    GraficoBarras(
                        etiquetas = EstadoIncidencia.values().map { it.textoUI() },
                        valores = EstadoIncidencia.values().map { e ->
                            distEstados.firstOrNull { it.estado == e }?.total ?: 0
                        }
                    )
                }
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = graficoBg
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Incidencias por gravedad", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Nivel de impacto reportado.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    GraficoBarras(
                        etiquetas = Gravedad.values().map { it.name },
                        valores = Gravedad.values().map { g ->
                            distGravedades.firstOrNull { it.gravedad == g }?.total ?: 0
                        }
                    )
                }
            }

            OutlinedButton(
                onClick = alVolver,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Volver") }
        }
    }
}

@Composable
private fun ResumenCardPro(
    titulo: String,
    valor: String,
    subtitulo: String,
    modifier: Modifier = Modifier,
    container: Color
) {
    ElevatedCard(
        modifier = modifier
            .heightIn(min = 120.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = container)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium)
                Text(valor, style = MaterialTheme.typography.headlineMedium)
                Text(
                    subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FiltroEstadoPro(
    seleccionado: EstadoIncidencia?,
    onSelected: (EstadoIncidencia?) -> Unit
) {
    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        ChipFiltro(
            text = "Todos",
            selected = seleccionado == null,
            onClick = { onSelected(null) },
            colors = chipColors
        )

        EstadoIncidencia.values().forEach { estado ->
            ChipFiltro(
                text = estado.textoChip(),
                selected = seleccionado == estado,
                onClick = { onSelected(estado) },
                colors = chipColors
            )
        }
    }
}

@Composable
private fun FiltroGravedadPro(
    seleccionado: Gravedad?,
    onSelected: (Gravedad?) -> Unit
) {
    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
        selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        ChipFiltro(
            text = "Todas",
            selected = seleccionado == null,
            onClick = { onSelected(null) },
            colors = chipColors
        )

        Gravedad.values().forEach { g ->
            ChipFiltro(
                text = g.name,
                selected = seleccionado == g,
                onClick = { onSelected(g) },
                colors = chipColors
            )
        }
    }
}

/**
 * ✅ Chip “pro” compatible:
 * En tu versión, el tipo correcto es ChipColors.
 */
@Composable
private fun ChipFiltro(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    colors: SelectableChipColors
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text, maxLines = 1) },
        modifier = Modifier.heightIn(min = 36.dp),
        colors = colors
    )
}

private fun EstadoIncidencia.textoUI(): String =
    when (this) {
        EstadoIncidencia.PENDIENTE -> "Pendiente"
        EstadoIncidencia.ACEPTADA -> "Aceptada"
        EstadoIncidencia.EN_REVISION -> "En revisión"
        EstadoIncidencia.RESUELTA -> "Resuelta"
        EstadoIncidencia.RECHAZADA -> "Rechazada"
    }

private fun EstadoIncidencia.textoChip(): String =
    when (this) {
        EstadoIncidencia.EN_REVISION -> "En rev."
        else -> textoUI()
    }
