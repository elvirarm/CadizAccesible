package com.example.cadizaccesible.ui.screens.reports

import app.cash.turbine.test
import com.example.cadizaccesible.data.db.dao.IncidenciaDao
import com.example.cadizaccesible.data.reports.EstadoIncidencia
import com.example.cadizaccesible.data.reports.Gravedad
import com.example.cadizaccesible.data.reports.RepositorioIncidenciasRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class InformesViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var dao: IncidenciaDao
    private lateinit var repo: RepositorioIncidenciasRoom
    private lateinit var vm: InformesViewModel

    private lateinit var totalIncFlow: MutableStateFlow<Int>
    private lateinit var totalUrgFlow: MutableStateFlow<Int>

    private val porEstado = mutableMapOf<EstadoIncidencia, Int>()
    private val porGravedad = mutableMapOf<Gravedad, Int>()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        dao = mock()

        totalIncFlow = MutableStateFlow(0)
        totalUrgFlow = MutableStateFlow(0)

        whenever(dao.totalIncidencias()).thenReturn(totalIncFlow)
        whenever(dao.totalUrgentes()).thenReturn(totalUrgFlow)

        // El VM los crea sí o sí, así que devolvemos vacío
        whenever(dao.distribucionPorEstado()).thenReturn(flowOf(emptyList()))
        whenever(dao.distribucionPorGravedad()).thenReturn(flowOf(emptyList()))

        whenever(dao.totalPorEstado(any())).thenAnswer { inv ->
            val estado = inv.arguments[0] as EstadoIncidencia
            flowOf(porEstado[estado] ?: 0)
        }

        whenever(dao.totalPorGravedad(any())).thenAnswer { inv ->
            val gravedad = inv.arguments[0] as Gravedad
            flowOf(porGravedad[gravedad] ?: 0)
        }

        repo = RepositorioIncidenciasRoom(dao)
        vm = InformesViewModel(repo)

        // Dataset base
        totalIncFlow.value = 10
        totalUrgFlow.value = 4

        porEstado[EstadoIncidencia.PENDIENTE] = 3
        porEstado[EstadoIncidencia.ACEPTADA] = 2
        porEstado[EstadoIncidencia.EN_REVISION] = 1
        porEstado[EstadoIncidencia.RESUELTA] = 4
        porEstado[EstadoIncidencia.RECHAZADA] = 0

        porGravedad[Gravedad.BAJA] = 5
        porGravedad[Gravedad.MEDIA] = 3
        porGravedad[Gravedad.ALTA] = 2
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `totalIncidencias emite el total`() = runTest {
        vm.totalIncidencias.test {
            assertEquals(0, awaitItem()) // initial del stateIn
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(10, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `totalUrgentes emite el total de urgentes`() = runTest {
        vm.totalUrgentes.test {
            assertEquals(0, awaitItem())
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(4, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setFiltroEstado actualiza ui`() = runTest {
        vm.ui.test {
            assertEquals(InformesUiState(), awaitItem())

            vm.setFiltroEstado(EstadoIncidencia.RESUELTA)
            dispatcher.scheduler.advanceUntilIdle()

            val next = awaitItem()
            assertEquals(EstadoIncidencia.RESUELTA, next.filtroEstado)
            assertEquals(null, next.filtroGravedad)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setFiltroGravedad actualiza ui`() = runTest {
        vm.ui.test {
            assertEquals(InformesUiState(), awaitItem())

            vm.setFiltroGravedad(Gravedad.ALTA)
            dispatcher.scheduler.advanceUntilIdle()

            val next = awaitItem()
            assertEquals(null, next.filtroEstado)
            assertEquals(Gravedad.ALTA, next.filtroGravedad)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `resumenFiltrado sin filtros usa totalIncidencias`() = runTest {
        vm.resumenFiltrado.test {
            assertEquals(0, awaitItem())
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(10, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `resumenFiltrado con filtroEstado usa totalPorEstado`() = runTest {
        vm.resumenFiltrado.test {
            assertEquals(0, awaitItem())
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(10, awaitItem())

            vm.setFiltroEstado(EstadoIncidencia.PENDIENTE)
            dispatcher.scheduler.advanceUntilIdle()

            assertEquals(3, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `resumenFiltrado con filtroGravedad usa totalPorGravedad`() = runTest {
        vm.resumenFiltrado.test {
            assertEquals(0, awaitItem())
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(10, awaitItem())

            vm.setFiltroGravedad(Gravedad.MEDIA)
            dispatcher.scheduler.advanceUntilIdle()

            assertEquals(3, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `cambio de filtros limpia el otro filtro`() = runTest {
        vm.ui.test {
            assertEquals(InformesUiState(), awaitItem())

            vm.setFiltroGravedad(Gravedad.ALTA)
            dispatcher.scheduler.advanceUntilIdle()
            val a = awaitItem()
            assertEquals(null, a.filtroEstado)
            assertEquals(Gravedad.ALTA, a.filtroGravedad)

            vm.setFiltroEstado(EstadoIncidencia.RESUELTA)
            dispatcher.scheduler.advanceUntilIdle()
            val b = awaitItem()

            // Requisito: al poner estado, gravedad debe quedar null
            assertEquals(EstadoIncidencia.RESUELTA, b.filtroEstado)
            assertEquals(null, b.filtroGravedad)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `porcentajeUrgentes calcula bien`() = runTest {
        // 4 urgentes de 10 => 40%
        vm.porcentajeUrgentes.test {
            assertEquals(0, awaitItem())
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(40, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

}
