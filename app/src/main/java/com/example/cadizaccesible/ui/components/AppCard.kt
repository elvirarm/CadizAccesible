package com.example.cadizaccesible.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.cadizaccesible.ui.theme.Dimens

/**
 * Componente de tarjeta personalizado basado en Material Design 3.
 * * Se utiliza como contenedor principal para agrupar información relacionada en la interfaz.
 * Encapsula la configuración de elevación, colores de superficie y espaciado interno
 * estándar definido en el sistema de diseño de la aplicación.
 * * @param title Título opcional que se muestra en la parte superior con estilo [titleMedium].
 * @param modifier [Modifier] para personalizar aspectos externos como el tamaño o el margen.
 * @param content El contenido que se renderizará dentro de la tarjeta, expuesto en un [ColumnScope].
 */
@Composable
fun AppCard(
    title: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(
            // Se utiliza el color 'surface' para asegurar legibilidad en modo claro y oscuro
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            // El padding se obtiene de la clase centralizada de dimensiones
            modifier = Modifier.padding(Dimens.CardPadding),
            content = {
                if (title != null) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = Dimens.SectionGap / 2) // Ajuste visual opcional
                    )
                }
                // Inyección del contenido dinámico
                content()
            }
        )
    }
}