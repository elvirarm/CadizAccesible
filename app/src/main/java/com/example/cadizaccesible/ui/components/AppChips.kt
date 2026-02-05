package com.example.cadizaccesible.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Componente para mostrar etiquetas informativas simples (Tags).
 * * Se utiliza generalmente para categorías o palabras clave que no implican un estado crítico.
 * * @param text El texto que se mostrará dentro del chip.
 * @param tonal Si es verdadero, utiliza el esquema de color 'surfaceVariant' para un aspecto más suave.
 */
@Composable
fun TagChip(
    text: String,
    tonal: Boolean = true
) {
    AssistChip(
        onClick = {}, // Deshabilitado para que funcione solo como etiqueta visual
        label = {
            Text(
                text = text,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        },
        colors = if (tonal) {
            AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            AssistChipDefaults.assistChipColors()
        },
        border = null
    )
}

/**
 * Clasificación semántica para los chips de estado.
 * Determina el impacto visual del componente según la urgencia o el éxito de la información.
 */
enum class StatusKind {
    /** Indica una acción completada con éxito o estado positivo. */
    Success,
    /** Indica una advertencia o estado que requiere atención. */
    Warning,
    /** Indica un error, urgencia alta o estado crítico. */
    Danger,
    /** Indica información informativa general sin carga semántica. */
    Neutral
}

/**
 * Componente avanzado de chip para mostrar estados (Status).
 * * A diferencia de [TagChip], este componente cambia dinámicamente sus colores
 * basándose en el [StatusKind] proporcionado, ayudando al usuario a identificar
 * rápidamente la gravedad o el estado de una incidencia.
 * * @param text Texto descriptivo del estado.
 * @param kind Categoría semántica ([StatusKind]) que dicta la paleta de colores a usar.
 */
@Composable
fun StatusChip(
    text: String,
    kind: StatusKind
) {
    // Selección dinámica de la pareja de colores (Contenedor y Contenido)
    val (container, label) = when (kind) {
        StatusKind.Success -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        StatusKind.Warning -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        StatusKind.Danger  -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        StatusKind.Neutral -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    AssistChip(
        onClick = {},
        label = {
            Text(
                text = text,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = container,
            labelColor = label
        ),
        border = null
    )
}