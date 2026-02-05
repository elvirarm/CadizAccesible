package com.example.cadizaccesible.ui.theme

import androidx.compose.runtime.mutableStateOf

/**
 * Objeto de estado global para gestionar la configuración dinámica del tema de la aplicación.
 * * Actúa como una fuente de verdad única y reactiva para la interfaz de usuario.
 * Al estar basado en el sistema de estados de Compose, cualquier componente que lea
 * [darkMode] se recompondrá automáticamente cuando su valor cambie.
 */
object AppThemeState {
    /**
     * Estado observable que determina si la aplicación debe renderizarse en modo oscuro.
     * * `true`: Se aplican los esquemas de color Dark de Material3.
     * `false`: Se utiliza el esquema de color Light estándar.
     * * Nota: Este valor suele ser actualizado por [ThemePrefs] durante el inicio de la app.
     */
    val darkMode = mutableStateOf(false)
}