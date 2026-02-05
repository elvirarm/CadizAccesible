package com.example.cadizaccesible.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Configuración del esquema de colores para el Tema Claro.
 * Mapea las constantes de color pastel a los roles semánticos de Material Design 3.
 */
private val LightCadizScheme = lightColorScheme(
    primary = PastelPrimary,
    onPrimary = PastelOnPrimary,
    primaryContainer = PastelPrimaryContainer,
    onPrimaryContainer = PastelOnPrimaryContainer,
    secondary = PastelSecondary,
    onSecondary = PastelOnSecondary,
    secondaryContainer = PastelSecondaryContainer,
    onSecondaryContainer = PastelOnSecondaryContainer,
    tertiary = PastelTertiary,
    onTertiary = PastelOnTertiary,
    tertiaryContainer = PastelTertiaryContainer,
    onTertiaryContainer = PastelOnTertiaryContainer,
    error = PastelError,
    onError = PastelOnError,
    background = PastelBackground,
    onBackground = PastelOnBackground,
    surface = PastelSurface,
    onSurface = PastelOnSurface,
    surfaceVariant = PastelSurfaceVariant,
    onSurfaceVariant = PastelOnSurfaceVariant,
    outline = PastelOutline
)

/**
 * Configuración del esquema de colores para el Tema Oscuro.
 * Mapea las constantes Dark para optimizar la visibilidad y el consumo de energía en modo noche.
 */
private val DarkCadizScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    error = DarkError,
    onError = DarkOnError,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline
)

/**
 * Definición de las formas geométricas (bordes redondeados) de los componentes de la app.
 * Sigue una escala progresiva desde elementos muy pequeños hasta contenedores grandes.
 */
private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(26.dp)
)

/**
 * Función Composable raíz para el tema de la aplicación.
 * * Este componente debe envolver la jerarquía de vistas en la Activity principal.
 * Provee automáticamente los colores, tipografía y formas a todos los componentes hijos.
 * * @param darkTheme Determina si se aplica el tema oscuro. Por defecto observa el estado
 * global reactivo en [AppThemeState.darkMode].
 * @param content El contenido de la aplicación que heredará este estilo.
 */
@Composable
fun CadizAccesibleTheme(
    darkTheme: Boolean = AppThemeState.darkMode.value,
    content: @Composable () -> Unit
) {
    // Selección dinámica del esquema de colores basada en el estado del tema
    val scheme = if (darkTheme) DarkCadizScheme else LightCadizScheme

    MaterialTheme(
        colorScheme = scheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}