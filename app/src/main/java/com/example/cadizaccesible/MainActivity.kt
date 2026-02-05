package com.example.cadizaccesible

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cadizaccesible.ui.AppRoot

/**
 * Punto de entrada principal (Entry Point) de la aplicación Android.
 * * Esta Activity actúa como el contenedor principal donde se aloja toda la interfaz
 * definida en Jetpack Compose. Al heredar de [ComponentActivity], proporciona las
 * herramientas necesarias para integrar el ciclo de vida de Android con el sistema de composición.
 */
class MainActivity : ComponentActivity() {

    /**
     * Se ejecuta cuando la actividad es creada por primera vez por el sistema.
     * * @param savedInstanceState Si la actividad se está re-inicializando después de haber sido
     * cerrada previamente, este Bundle contiene los datos más recientes suministrados.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Habilita el modo 'Edge-to-Edge' (de borde a borde).
         * * Esta función permite que la aplicación dibuje contenido debajo de las barras del sistema
         * (barra de estado y barra de navegación), aprovechando toda la superficie de la pantalla
         * para una experiencia más inmersiva. Se complementa con WindowInsets en los Scaffolds.
         */
        enableEdgeToEdge()

        /**
         * Define la jerarquía de la interfaz de usuario mediante Composición.
         * * En lugar de usar archivos XML tradicionales (setContentView), se utiliza [setContent]
         * para declarar que el nodo raíz de la UI es el componente [AppRoot].
         */
        setContent {
            // AppRoot suele contener el Theme personalizado y el Host de Navegación
            AppRoot()
        }
    }
}