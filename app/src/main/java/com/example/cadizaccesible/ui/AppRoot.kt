package com.example.cadizaccesible.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.cadizaccesible.data.sesion.ThemePrefs
import com.example.cadizaccesible.ui.navigation.HostNavegacion
import com.example.cadizaccesible.ui.theme.CadizAccesibleTheme

/**
 * Componente raíz de la jerarquía de Compose.
 * * Se encarga de orquestar los elementos globales de la aplicación:
 * 1. Recupera las preferencias del usuario (Tema oscuro/claro).
 * 2. Inyecta el sistema de diseño personalizado [CadizAccesibleTheme].
 * 3. Establece el contenedor base [Surface].
 * 4. Inicializa el grafo de navegación principal mediante [HostNavegacion].
 */
@Composable
fun AppRoot() {
    val context = LocalContext.current

    // Se utiliza 'remember' para evitar recrear la instancia de ThemePrefs en cada recomposición.
    val prefs = remember { ThemePrefs(context) }

    /**
     * Observa el flujo del modo oscuro proveniente de DataStore.
     * Al usar 'collectAsState', cualquier cambio en las preferencias provocará que
     * toda la aplicación se redibuje automáticamente con el nuevo tema.
     */
    val darkMode by prefs.darkModeFlow.collectAsState(initial = false)

    // Aplicación del tema personalizado envolviendo toda la interfaz.
    CadizAccesibleTheme(darkTheme = darkMode) {
        /**
         * Contenedor base que utiliza el color de fondo definido en el esquema
         * de colores actual (PastelBackground o DarkBackground).
         */
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Punto de entrada para el sistema de navegación por pantallas.
            HostNavegacion()
        }
    }
}