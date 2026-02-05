package com.example.cadizaccesible.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cadizaccesible.ui.theme.Dimens

/**
 * Tarjeta de gran impacto visual (Hero) utilizada para secciones destacadas.
 * * A diferencia de [AppCard], este componente utiliza el color de contenedor primario
 * del sistema para atraer la atención del usuario y permite la inclusión de un elemento
 * gráfico o icono principal (hero content).
 * * @param title Título principal en estilo [titleLarge].
 * @param subtitle Subtítulo descriptivo con opacidad reducida para jerarquía visual.
 * @param modifier [Modifier] para ajustar el tamaño o comportamiento de la tarjeta.
 * @param hero Slot opcional para incluir un Composable (como un icono grande o imagen)
 * que aparecerá en la parte superior del texto.
 */
@Composable
fun HeroCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    hero: (@Composable () -> Unit)? = null
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimens.CardPadding),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Renderiza el contenido destacado (ej. un Icono con Modifier.size(64.dp))
            if (hero != null) {
                hero()
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
            )
        }
    }
}