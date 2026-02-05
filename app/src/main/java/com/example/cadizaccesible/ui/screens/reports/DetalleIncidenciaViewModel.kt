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

/**
 * Estado de la interfaz para la visualización y edición de detalles de una incidencia.
 * @property cargando Indica si la incidencia se está recuperando de la base de datos por primera vez.
 * @property actualizando Estado activo durante la persistencia de cambios (ej. cambio de estado por un admin).
 * @property comentarioAdmin Buffer temporal para el texto de respuesta del administrador.
 */
data class DetalleIncidenciaUiState(
    val cargando: Boolean = true,
    val incidencia: Incidencia? = null,
    val comentarioAdmin: String = "",
    val error: String = "",
    val actualizando: Boolean = false
)

/**
 * ViewModel que gestiona la lógica de una incidencia específica identificada por su ID.
 * * Utiliza una [Factory] para permitir la inyección del [idIncidencia] en tiempo de creación,
 * lo que facilita la carga automática de datos en el bloque [init].
 */
class DetalleIncidenciaViewModel(
    private val repo: RepositorioIncidenciasRoom,
    private val idIncidencia: String
) : ViewModel() {

    private val _ui = MutableStateFlow(DetalleIncidenciaUiState())
    val ui: StateFlow<DetalleIncidenciaUiState> = _ui

    init {
        cargar()
    }

    /** Recupera la incidencia desde el repositorio y actualiza el estado de la UI. */
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

    /** Actualiza el estado local del comentario antes de ser persistido. */
    fun onComentarioAdmin(v: String) {
        _ui.update { it.copy(comentarioAdmin = v) }
    }

    /**
     * Persiste un cambio de estado y el comentario asociado en la base de datos.
     * * Tras la actualización, vuelve a consultar la base de datos para asegurar que
     * la UI refleja los datos exactos que han sido guardados.
     */
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

    /** Factoría necesaria para pasar argumentos dinámicos al constructor del ViewModel. */
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