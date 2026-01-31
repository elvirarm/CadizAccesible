package com.example.cadizaccesible.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cadizaccesible.data.reports.Gravedad
import com.example.cadizaccesible.data.reports.RepositorioIncidenciasRoom
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

class CrearIncidenciaViewModel(
    private val repo: RepositorioIncidenciasRoom
) : ViewModel() {

    private val _ui = MutableStateFlow(CrearIncidenciaUiState())
    val ui: StateFlow<CrearIncidenciaUiState> = _ui

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

    fun publicar(emailUsuario: String) {
        val s = _ui.value
        val t = s.titulo.trim()
        val d = s.descripcion.trim()
        val dir = s.direccionTexto.trim()

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

    fun consumirCreadaOk() = _ui.update { it.copy(creadaOk = false) }
}
