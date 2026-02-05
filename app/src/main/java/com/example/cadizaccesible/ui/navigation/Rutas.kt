package com.example.cadizaccesible.ui.navigation

/**
 * Definición centralizada de las rutas de navegación de la aplicación.
 * * Utiliza una [sealed class] para garantizar que todas las rutas sean conocidas en tiempo de compilación,
 * proporcionando una estructura segura y organizada para el NavHost.
 * * @property ruta Cadena de texto que identifica la ruta técnica en el grafo de navegación.
 */
sealed class Rutas(val ruta: String) {

    // --- Flujo de Autenticación ---

    /** Pantalla de presentación y verificación de estado de sesión. */
    data object Splash : Rutas("splash")

    /** Pantalla de inicio de sesión. */
    data object Login : Rutas("login")

    /** Formulario para la creación de nuevas cuentas de usuario. */
    data object Registro : Rutas("registro")

    // --- Pantallas de Inicio (Dashboards) ---

    /** Panel principal para usuarios con rol CIUDADANO. */
    data object InicioCiudadano : Rutas("inicio_ciudadano")

    /** Panel principal para usuarios con rol ADMIN. */
    data object InicioAdmin : Rutas("inicio_admin")

    // --- Gestión de Incidencias ---

    /** Listado filtrado de incidencias reportadas por el usuario actual. */
    data object MisIncidencias : Rutas("mis_incidencias")

    /** Listado global de incidencias para gestión administrativa. */
    data object BandejaIncidencias : Rutas("bandeja_incidencias")

    /** * Pantalla de información detallada de una incidencia específica.
     * Requiere un parámetro dinámico {id}.
     */
    data object DetalleIncidencia : Rutas("detalle_incidencia/{id}") {
        /** * Construye la ruta final sustituyendo el marcador de posición por un ID real.
         * @param id Identificador único de la incidencia a mostrar.
         */
        fun crearRuta(id: String) = "detalle_incidencia/$id"
    }

    /** Formulario para registrar un nuevo problema de accesibilidad. */
    data object CrearIncidencia : Rutas("crear_incidencia")

    // --- Análisis ---

    /** Pantalla de visualización de estadísticas y gráficos de gestión. */
    data object Informes : Rutas("informes")
}