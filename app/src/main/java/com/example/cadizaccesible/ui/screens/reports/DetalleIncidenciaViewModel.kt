package com.example.cadizaccesible.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cadizaccesible.data.reports.EstadoIncidencia
import com.example.cadizaccesible.data.reports.Incidencia
import com.example.cadizaccesible.data.reports.RepositorioIncidenciasRoom
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetalleIncidenciaUiState(
    val cargando: Boolean = true,
    val incidencia: Incidencia? = null,
    val comentarioAdmin: String = "",
    val error: String = "",
    val actualizando: Boolean = false
)

class DetalleIncidenciaViewModel(
    private val repo: RepositorioIncidenciasRoom,
    private val idIncidencia: String
) : ViewModel() {

    private val _ui = MutableStateFlow(DetalleIncidenciaUiState())
    val ui: StateFlow<DetalleIncidenciaUiState> = _ui

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _ui.update { it.copy(cargando = true, error = "") }
            val inc = repo.obtenerPorId(idIncidencia)
            if (inc == null) {
                _ui.update { it.copy(cargando = false, incidencia = null, error = "No se encontró la incidencia.") }
            } else {
                _ui.update {
                    it.copy(
                        cargando = false,
                        incidencia = inc,
                        comentarioAdmin = inc.comentarioAdmin,
                        error = ""
                    )
                }
            }
        }
    }

    fun onComentarioAdmin(v: String) {
        _ui.update { it.copy(comentarioAdmin = v) }
    }

    fun cambiarEstado(estado: EstadoIncidencia, onOk: (() -> Unit)? = null) {
        val inc = _ui.value.incidencia ?: return
        viewModelScope.launch {
            _ui.update { it.copy(actualizando = true, error = "") }
            runCatching {
                repo.actualizarEstado(
                    id = inc.id,
                    nuevoEstado = estado,
                    comentarioAdmin = _ui.value.comentarioAdmin
                )
            }.onSuccess {
                val updated = repo.obtenerPorId(inc.id)
                _ui.update {
                    it.copy(
                        actualizando = false,
                        incidencia = updated,
                        error = ""
                    )
                }
                onOk?.invoke()
            }.onFailure {
                _ui.update { it.copy(actualizando = false, error = "No se pudo actualizar el estado.") }
            }
        }
    }

    class Factory(
        private val repo: RepositorioIncidenciasRoom,
        private val id: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetalleIncidenciaViewModel(repo, id) as T
        }
    }
}
