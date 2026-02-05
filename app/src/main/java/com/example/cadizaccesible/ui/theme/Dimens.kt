package com.example.cadizaccesible.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Define las dimensiones y espaciados estándar utilizados en toda la aplicación.
 * * Este objeto centraliza las medidas de padding, márgenes y radios de curvatura
 * para garantizar una experiencia visual coherente y facilitar cambios globales
 * en la densidad de la interfaz.
 */
object Dimens {
    /** Margen estándar entre el contenido principal y los bordes de la pantalla. */
    val ScreenPadding = 18.dp

    /** Espaciado vertical u horizontal entre secciones lógicas de una pantalla. */
    val SectionGap = 14.dp

    /** Padding interno para los componentes de tipo tarjeta ([AppCard]). */
    val CardPadding = 16.dp

    /** Espacio de separación entre tarjetas adyacentes en una lista o cuadrícula. */
    val CardGap = 12.dp

    /** Radio de curvatura para los elementos visuales destacados o cabeceras (Hero). */
    val HeroRadius = 22.dp
}