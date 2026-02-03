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

@Composable
fun GraficoBarras(
    etiquetas: List<String>,
    valores: List<Int>,
    modifier: Modifier = Modifier.fillMaxWidth(),
    alturaDp: Int = 160,
    titulo: String? = null
) {
    val safeValores = if (valores.isEmpty()) listOf(0) else valores
    val safeEtiquetas = if (etiquetas.isEmpty()) List(safeValores.size) { "" } else etiquetas

    val maxV = (safeValores.maxOrNull() ?: 0).coerceAtLeast(1)

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

                val leftPad = 12f
                val rightPad = 12f
                val topPad = 10f
                val bottomPad = 22f

                val chartW = w - leftPad - rightPad
                val chartH = h - topPad - bottomPad
                val yBase = topPad + chartH

                drawLine(
                    color = axisColor,
                    start = Offset(leftPad, yBase),
                    end = Offset(leftPad + chartW, yBase),
                    strokeWidth = 2f
                )

                fun yFor(v: Int): Float {
                    val ratio = v.toFloat() / maxV.toFloat()
                    return yBase - (ratio * chartH)
                }

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

                    drawRoundRect(
                        color = axisColor.copy(alpha = 0.08f),
                        topLeft = Offset(x, topPad),
                        size = Size(barW, chartH),
                        cornerRadius = CornerRadius(12f, 12f)
                    )

                    if (barH > 0f) {
                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(x, yBase - barH),
                            size = Size(barW, barH),
                            cornerRadius = CornerRadius(12f, 12f)
                        )

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
