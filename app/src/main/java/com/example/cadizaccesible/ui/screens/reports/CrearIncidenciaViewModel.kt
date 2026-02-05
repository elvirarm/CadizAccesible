package com.example.cadizaccesible.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cadizaccesible.data.reports.Gravedad
import com.example.cadizaccesible.data.reports.RepositorioIncidenciasRoom
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Representa el estado íntegro del formulario de creación de una incidencia.
 * * @property publicando Indica si el proceso de guardado en la DB está activo.
 * @property creadaOk Flag de señalización para que la UI sepa cuándo navegar hacia atrás.
 * @property error Mensaje de validación o error técnico para mostrar al usuario.
 */
data class CrearIncidenciaUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "Aceras",
    val accesibilidadAfectada: String = "Movilidad",
    val gravedad: Gravedad = Gravedad.MEDIA,
    val esUrgente: Boolean = false,
    val esObstaculoTemporal: Boolean = false,
    val direccionTexto: String = "",
    val latitud: Double? = null,
    val longitud: Double? = null,
    val fotoUri: String? = null,

    val publicando: Boolean = false,
    val error: String = "",
    val creadaOk: Boolean = false
)

/**
 * ViewModel que orquesta la lógica de recolección de datos y validación para nuevos reportes.
 * * Implementa funciones granulares para actualizar cada campo del estado, asegurando que
 * los errores se limpien automáticamente cuando el usuario corrige la entrada.
 * * @property repo Repositorio de incidencias para la persistencia en Room.
 */

class CrearIncidenciaViewModel(
    private val repo: RepositorioIncidenciasRoom
) : ViewModel() {

    private val _ui = MutableStateFlow(CrearIncidenciaUiState())
    val ui: StateFlow<CrearIncidenciaUiState> = _ui

    // --- Funciones de actualización de estado (UDF) ---

    fun onTitulo(v: String) = _ui.update { it.copy(titulo = v, error = "") }
    fun onDescripcion(v: String) = _ui.update { it.copy(descripcion = v, error = "") }
    fun onCategoria(v: String) = _ui.update { it.copy(categoria = v) }
    fun onAccesibilidad(v: String) = _ui.update { it.copy(accesibilidadAfectada = v) }
    fun onGravedad(v: Gravedad) = _ui.update { it.copy(gravedad = v) }
    fun onUrgente(v: Boolean) = _ui.update { it.copy(esUrgente = v) }
    fun onObstaculo(v: Boolean) = _ui.update { it.copy(esObstaculoTemporal = v) }
    fun onDireccion(v: String) = _ui.update { it.copy(direccionTexto = v, error = "") }
    fun onUbicacion(lat: Double?, lon: Double?) = _ui.update { it.copy(latitud = lat, longitud = lon) }
    fun onFoto(uri: String?) = _ui.update { it.copy(fotoUri = uri) }

    /**
     * Valida los campos obligatorios e intenta persistir la incidencia.
     * * Realiza un trim de los textos y comprueba que no estén vacíos. Si la validación
     * es exitosa, lanza una corrutina para interactuar con la base de datos.
     * * @param emailUsuario El identificador del ciudadano que reporta (obtenido de la sesión).
     */
    fun publicar(emailUsuario: String) {
        val s = _ui.value
        val t = s.titulo.trim()
        val d = s.descripcion.trim()
        val dir = s.direccionTexto.trim()

        // Lógica de validación de negocio

        if (t.isBlank()) { _ui.update { it.copy(error = "El título es obligatorio.") }; return }
        if (d.isBlank()) { _ui.update { it.copy(error = "La descripción es obligatoria.") }; return }
        if (dir.isBlank()) { _ui.update { it.copy(error = "La ubicación es obligatoria (aunque sea aproximada).") }; return }

        viewModelScope.launch {
            _ui.update { it.copy(publicando = true, error = "") }
            runCatching {
                repo.crearIncidencia(
                    emailCreador = emailUsuario,
                    titulo = t,
                    descripcion = d,
                    categoria = s.categoria,
                    accesibilidadAfectada = s.accesibilidadAfectada,
                    gravedad = s.gravedad,
                    esUrgente = s.esUrgente,
                    esObstaculoTemporal = s.esObstaculoTemporal,
                    direccionTexto = dir,
                    latitud = s.latitud,
                    longitud = s.longitud,
                    fotoUri = s.fotoUri
                )
            }.onSuccess {
                _ui.update { it.copy(publicando = false, creadaOk = true) }
            }.onFailure {
                _ui.update { it.copy(publicando = false, error = "No se pudo guardar la incidencia.") }
            }
        }
    }

    /** Resetea el flag de éxito tras la navegación. */
    fun consumirCreadaOk() = _ui.update { it.copy(creadaOk = false) }
}
