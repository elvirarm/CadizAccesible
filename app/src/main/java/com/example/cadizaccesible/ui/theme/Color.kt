package com.example.cadizaccesible.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Definición de la paleta de colores para el Tema Claro (Pastel).
 * Se caracteriza por tonos suaves y fondos claros para mejorar la legibilidad
 * en exteriores y entornos muy iluminados.
 */
val PastelPrimary = Color(0xFF1E4DB7)          // Color principal (Botones, elementos destacados)
val PastelOnPrimary = Color(0xFFFFFFFF)        // Texto/Iconos sobre color principal
val PastelPrimaryContainer = Color(0xFFF4EBDD) // Fondos de énfasis suave
val PastelOnPrimaryContainer = Color(0xFF3A2F1D)

val PastelSecondary = Color(0xFF6EC6E8)        // Colores de acento menos prominentes
val PastelOnSecondary = Color(0xFF0B2A33)
val PastelSecondaryContainer = Color(0xFFDDF2F9)
val PastelOnSecondaryContainer = Color(0xFF12343F)

val PastelTertiary = Color(0xFFFF9EB8)         // Tercer color para contrastes específicos
val PastelOnTertiary = Color(0xFF3A0C1C)
val PastelTertiaryContainer = Color(0xFFFFE4EC)
val PastelOnTertiaryContainer = Color(0xFF5A1A2D)

val PastelError = Color(0xFFB3261E)            // Color para estados de error o alertas
val PastelOnError = Color(0xFFFFFFFF)

val PastelBackground = Color(0xFFFAF9F6)       // Fondo principal de las pantallas
val PastelOnBackground = Color(0xFF1C1B1F)

val PastelSurface = Color(0xFFFFFFFF)          // Superficie de componentes (Cards, Sheets)
val PastelOnSurface = Color(0xFF1C1B1F)

val PastelSurfaceVariant = Color(0xFFF7F1E7)   // Alternativa para diferenciar capas de UI
val PastelOnSurfaceVariant = Color(0xFF4A4032)

val PastelOutline = Color(0xFFE0D6C8)          // Bordes y separadores

// Colores semánticos adicionales
val PastelSuccess = Color(0xFF2E7D32)          // Éxito (Ej: Incidencia resuelta)
val PastelWarning = Color(0xFFB26A00)          // Advertencia (Ej: Pendiente de revisión)


/**
 * Definición de la paleta de colores para el Tema Oscuro (Dark).
 * Optimizado para reducir la fatiga visual y ahorrar batería en pantallas OLED.
 * Utiliza tonos de azul profundo y grisáceos para mantener la coherencia con la app.
 */
val DarkBackground = Color(0xFF0E1426)         // Fondo profundo (Casi negro/azul noche)
val DarkOnBackground = Color(0xFFEAEAF0)

val DarkSurface = Color(0xFF121A30)            // Superficie elevada en modo oscuro
val DarkOnSurface = Color(0xFFEAEAF0)

val DarkSurfaceVariant = Color(0xFF182647)     // Contraste para elementos secundarios
val DarkOnSurfaceVariant = Color(0xFFD2DBF0)

val DarkPrimary = Color(0xFF8FB0FF)            // Versión más clara del primario para accesibilidad
val DarkOnPrimary = Color(0xFF0B1630)

val DarkPrimaryContainer = Color(0xFF2E2A24)
val DarkOnPrimaryContainer = Color(0xFFF2E8D8)

val DarkSecondary = Color(0xFF78D3F2)
val DarkOnSecondary = Color(0xFF06222B)

val DarkSecondaryContainer = Color(0xFF162B4D)
val DarkOnSecondaryContainer = Color(0xFFE6F0FF)

val DarkTertiary = Color(0xFFFFB6C9)
val DarkOnTertiary = Color(0xFF3A0C1C)

val DarkTertiaryContainer = Color(0xFF4A2030)
val DarkOnTertiaryContainer = Color(0xFFFFE4EC)

val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)

val DarkOutline = Color(0xFF3E4A6B)