package com.example.cadizaccesible.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

/**
 * Gráfico de barras personalizado para representar estadísticas de incidencias.
 * * Este componente dibuja un gráfico de barras reactivo dentro de una [ElevatedCard].
 * Incluye ejes automáticos, líneas de cuadrícula y etiquetas descriptivas.
 * * @param etiquetas Lista de nombres para cada barra (ej. estados de incidencia).
 * @param valores Lista de cantidades numéricas correspondientes a cada etiqueta.
 * @param modifier [Modifier] para ajustar el contenedor del gráfico.
 * @param alturaDp Altura fija para el área de dibujo del Canvas.
 * @param titulo Encabezado opcional para describir el propósito del gráfico.
 */
@Composable
fun GraficoBarras(
    etiquetas: List<String>,
    valores: List<Int>,
    modifier: Modifier = Modifier.fillMaxWidth(),
    alturaDp: Int = 160,
    titulo: String? = null
) {
    // Manejo de seguridad para listas vacías
    val safeValores = if (valores.isEmpty()) listOf(0) else valores
    val safeEtiquetas = if (etiquetas.isEmpty()) List(safeValores.size) { "" } else etiquetas

    // Cálculo del valor máximo para escalar el eje Y proporcionalmente
    val maxV = (safeValores.maxOrNull() ?: 0).coerceAtLeast(1)

    // Definición de colores basada en el MaterialTheme (Modo claro/oscuro)
    val container = MaterialTheme.colorScheme.surfaceVariant
    val onContainer = MaterialTheme.colorScheme.onSurfaceVariant
    val axisColor = onContainer.copy(alpha = 0.22f)
    val gridColor = onContainer.copy(alpha = 0.14f)
    val barColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.88f)
    val barAccent = MaterialTheme.colorScheme.secondary.copy(alpha = 0.90f)

    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(containerColor = container)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (!titulo.isNullOrBlank()) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(alturaDp.dp)
            ) {
                val w = size.width
                val h = size.height

                // Márgenes internos para el área del gráfico
                val leftPad = 12f
                val rightPad = 12f
                val topPad = 10f
                val bottomPad = 22f

                val chartW = w - leftPad - rightPad
                val chartH = h - topPad - bottomPad
                val yBase = topPad + chartH

                // Dibujado del eje X (línea base)
                drawLine(
                    color = axisColor,
                    start = Offset(leftPad, yBase),
                    end = Offset(leftPad + chartW, yBase),
                    strokeWidth = 2f
                )

                /** Función auxiliar para mapear un valor numérico a una coordenada Y en el Canvas. */
                fun yFor(v: Int): Float {
                    val ratio = v.toFloat() / maxV.toFloat()
                    return yBase - (ratio * chartH)
                }

                // Dibujado de líneas de cuadrícula horizontales (0, 50%, 100%)
                listOf(0, maxV / 2, maxV).distinct().forEach { m ->
                    val y = yFor(m)
                    drawLine(
                        color = gridColor,
                        start = Offset(leftPad, y),
                        end = Offset(leftPad + chartW, y),
                        strokeWidth = 1f
                    )
                }

                val n = max(1, safeValores.size)
                val gap = 10f
                val barW = ((chartW - gap * (n + 1)) / n).coerceAtLeast(10f)

                safeValores.forEachIndexed { i, v ->
                    val x = leftPad + gap + i * (barW + gap)
                    val ratio = v.toFloat() / maxV.toFloat()
                    val barH = ratio * chartH

                    // Dibujado de fondo de la barra (guía visual)
                    drawRoundRect(
                        color = axisColor.copy(alpha = 0.08f),
                        topLeft = Offset(x, topPad),
                        size = Size(barW, chartH),
                        cornerRadius = CornerRadius(12f, 12f)
                    )

                    if (barH > 0f) {
                        // Dibujado de la barra principal
                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(x, yBase - barH),
                            size = Size(barW, barH),
                            cornerRadius = CornerRadius(12f, 12f)
                        )

                        // Detalle visual: acento en la parte superior de la barra
                        val capH = (barH * 0.18f).coerceIn(6f, 14f)
                        drawRoundRect(
                            color = barAccent,
                            topLeft = Offset(x, yBase - barH),
                            size = Size(barW, capH),
                            cornerRadius = CornerRadius(12f, 12f)
                        )
                    }
                }
            }

            // Fila de etiquetas (Nombres de los estados)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val count = min(safeEtiquetas.size, safeValores.size)
                for (i in 0 until count) {
                    Text(
                        text = safeEtiquetas[i].replace("_", "\n"),
                        style = MaterialTheme.typography.bodySmall,
                        color = onContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                        maxLines = 2
                    )
                }
            }

            // Fila de valores (Cantidades numéricas)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                safeValores.forEach { v ->
                    Text(
                        text = v.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = onContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )
                }
            }
        }
    }
}