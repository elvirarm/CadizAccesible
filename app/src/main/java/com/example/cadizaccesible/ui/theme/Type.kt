package com.example.cadizaccesible.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Definición de la jerarquía tipográfica de la aplicación.
 * * Este objeto configura los estilos de texto estandarizados por Material Design 3,
 * asegurando una legibilidad óptima y una estructura visual coherente en todas las pantallas.
 */
val AppTypography = Typography(
    /** Estilo para títulos principales o cabeceras de pantalla de gran tamaño. */
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    /** Estilo para subtítulos o títulos de secciones dentro de tarjetas ([AppCard]). */
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    /** Estilo estándar para bloques de texto largo o descripciones principales. */
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    /** Estilo para texto secundario, detalles técnicos o información menos relevante. */
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    /** Estilo para etiquetas de botones, badges o textos informativos pequeños. */
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp
    )
)