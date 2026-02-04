package com.example.cadizaccesible.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cadizaccesible.data.reports.*
import kotlinx.coroutines.flow.*

data class InformesUiState(
    val filtroEstado: EstadoIncidencia? = null,
    val filtroGravedad: Gravedad? = null
)

class InformesViewModel(
    private val repo: RepositorioIncidenciasRoom
) : ViewModel() {

    private val _ui = MutableStateFlow(InformesUiState())
    val ui: StateFlow<InformesUiState> = _ui

    fun setFiltroEstado(v: EstadoIncidencia?) =
        _ui.update { it.copy(filtroEstado = v, filtroGravedad = null) }

    fun setFiltroGravedad(v: Gravedad?) =
        _ui.update { it.copy(filtroGravedad = v, filtroEstado = null) }

    val totalIncidencias =
        repo.totalIncidencias().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalUrgentes =
        repo.totalUrgentes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val distEstados =
        repo.distribucionPorEstado().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val distGravedades =
        repo.distribucionPorGravedad().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val porcentajeUrgentes =
        combine(totalUrgentes, totalIncidencias) { urg, total ->
            if (total == 0) 0 else (urg * 100) / total
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val resumenFiltrado =
        ui.flatMapLatest { f ->
            when {
                f.filtroEstado != null -> repo.totalPorEstado(f.filtroEstado)
                f.filtroGravedad != null -> repo.totalPorGravedad(f.filtroGravedad)
                else -> repo.totalIncidencias()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    class Factory(private val repo: RepositorioIncidenciasRoom) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return InformesViewModel(repo) as T
        }
    }
}
