package com.example.cadizaccesible.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cadizaccesible.data.reports.*
import kotlinx.coroutines.flow.*

/**
 * Representa el estado de los filtros aplicados en la pantalla de informes.
 * @property filtroEstado Estado de incidencia seleccionado para filtrar, o null si no hay filtro.
 * @property filtroGravedad Nivel de gravedad seleccionado para filtrar, o null si no hay filtro.
 */
data class InformesUiState(
    val filtroEstado: EstadoIncidencia? = null,
    val filtroGravedad: Gravedad? = null
)

/**
 * ViewModel encargado de procesar la analítica de datos de la aplicación.
 * * Utiliza programación reactiva para transformar los flujos de la base de datos (Room)
 * en información estadística procesada para la interfaz de usuario.
 * * @property repo Repositorio de incidencias para acceso a datos locales.
 */
class InformesViewModel(
    private val repo: RepositorioIncidenciasRoom
) : ViewModel() {

    /** Estado interno de los filtros de la interfaz. */
    private val _ui = MutableStateFlow(InformesUiState())
    /** Flujo público del estado de filtros. */
    val ui: StateFlow<InformesUiState> = _ui

    /** * Actualiza el filtro por estado.
     * Nota: Los filtros de estado y gravedad son excluyentes en esta implementación.
     */
    fun setFiltroEstado(v: EstadoIncidencia?) =
        _ui.update { st ->
            if (v == null) st.copy(filtroEstado = null)
            else st.copy(filtroEstado = v, filtroGravedad = null)
        }

    /** * Actualiza el filtro por gravedad.
     * Nota: Al activar este filtro, se limpia el filtro de estado.
     */
    fun setFiltroGravedad(v: Gravedad?) =
        _ui.update { st ->
            if (v == null) st.copy(filtroGravedad = null)
            else st.copy(filtroGravedad = v, filtroEstado = null)
        }

    /** Flujo del total de incidencias registradas en el sistema. */
    val totalIncidencias =
        repo.totalIncidencias().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /** Flujo del conteo de incidencias marcadas como urgentes. */
    val totalUrgentes =
        repo.totalUrgentes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /** Lista de recuentos agrupados por el estado de la incidencia (para gráficos). */
    val distEstados =
        repo.distribucionPorEstado().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Lista de recuentos agrupados por nivel de gravedad (para gráficos). */
    val distGravedades =
        repo.distribucionPorGravedad().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** * Cálculo reactivo del porcentaje de urgencias sobre el total.
     * Combina dos flujos de datos y recalcula automáticamente cuando cualquiera cambia.
     */
    val porcentajeUrgentes =
        combine(totalUrgentes, totalIncidencias) { urg, total ->
            if (total == 0) 0 else (urg * 100) / total
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /** * Conteo dinámico basado en los filtros seleccionados en la UI.
     * Utiliza [flatMapLatest] para cambiar de una consulta a otra en el repositorio
     * dependiendo de si el usuario filtra por estado, por gravedad o si no hay filtro.
     */
    val resumenFiltrado =
        ui.flatMapLatest { f ->
            when {
                f.filtroEstado != null -> repo.totalPorEstado(f.filtroEstado)
                f.filtroGravedad != null -> repo.totalPorGravedad(f.filtroGravedad)
                else -> repo.totalIncidencias()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /** * Factoría para la creación del ViewModel inyectando el repositorio.
     */
    class Factory(private val repo: RepositorioIncidenciasRoom) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return InformesViewModel(repo) as T
        }
    }
}