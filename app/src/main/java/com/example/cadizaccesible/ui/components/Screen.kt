package com.example.cadizaccesible.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.cadizaccesible.ui.theme.Dimens

/**
 * Contenedor base estandarizado para todas las pantallas de la aplicación.
 * * Este componente asegura que cada vista respete el esquema de colores, los márgenes
 * laterales de la marca ([Dimens.ScreenPadding]) y el espaciado entre secciones ([Dimens.SectionGap]).
 * * @param modifier [Modifier] para personalizaciones adicionales del contenedor.
 * @param scroll Determina si la pantalla debe permitir el desplazamiento vertical.
 * Es útil desactivarlo en pantallas fijas como mapas o dashboards con listas internas propias.
 * @param content El contenido de la pantalla, expuesto en un [ColumnScope].
 */
@Composable
fun Screen(
    modifier: Modifier = Modifier,
    scroll: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        // Asegura que el fondo cambie según el tema (Claro/Oscuro)
        color = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.ScreenPadding)
                // Aplicación condicional del modificador de scroll
                .then(if (scroll) Modifier.verticalScroll(rememberScrollState()) else Modifier),
            // Espaciado vertical automático entre elementos directos de la pantalla
            verticalArrangement = Arrangement.spacedBy(Dimens.SectionGap),
            content = content
        )
    }
}