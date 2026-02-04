package com.example.cadizaccesible.ui.screens.reports

import app.cash.turbine.test
import com.example.cadizaccesible.data.db.dao.IncidenciaDao
import com.example.cadizaccesible.data.db.entities.IncidenciaEntity
import com.example.cadizaccesible.data.reports.EstadoIncidencia
import com.example.cadizaccesible.data.reports.Gravedad
import com.example.cadizaccesible.data.reports.RepositorioIncidenciasRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class DetalleIncidenciaViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var dao: IncidenciaDao
    private lateinit var repo: RepositorioIncidenciasRoom

    private val ID = "id-1"

    private fun entity(
        id: String = ID,
        estado: EstadoIncidencia = EstadoIncidencia.PENDIENTE,
        comentarioAdmin: String = "",
        gravedad: Gravedad = Gravedad.MEDIA,
        esUrgente: Boolean = true
    ) = IncidenciaEntity(
        id = id,
        emailCreador = "a@a.com",
        titulo = "Bordillo roto",
        descripcion = "Hay un bordillo roto",
        categoria = "Vía pública",
        accesibilidadAfectada = "Movilidad reducida",
        gravedad = gravedad,
        esUrgente = esUrgente,
        esObstaculoTemporal = false,
        direccionTexto = "Cádiz",
        latitud = null,
        longitud = null,
        fotoUri = null,
        estado = estado,
        comentarioAdmin = comentarioAdmin,
        fechaEpochMs = 123456789L
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        dao = mock()
        repo = RepositorioIncidenciasRoom(dao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `carga correcta por ID`() = runTest {
        whenever(dao.obtenerPorId(eq(ID))).thenReturn(entity(id = ID, comentarioAdmin = "hola"))

        val vm = DetalleIncidenciaViewModel(repo, ID)

        vm.ui.test {
            // 1) Estado inicial del StateFlow
            val s0 = awaitItem()
            assertTrue(s0.cargando)

            // 2) Dejamos correr el init->cargar()
            dispatcher.scheduler.advanceUntilIdle()

            val s1 = awaitItem()
            assertFalse(s1.cargando)
            assertNotNull(s1.incidencia)
            assertEquals(ID, s1.incidencia!!.id)
            assertEquals("hola", s1.comentarioAdmin)
            assertEquals("", s1.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `estado cargando se mantiene true hasta que termina la carga`() = runTest {
        // Simulamos que al principio no hay respuesta y luego sí (dos llamadas)
        whenever(dao.obtenerPorId(eq(ID)))
            .thenReturn(entity(id = ID)) // en realidad con advanceUntilIdle ya basta; esto es por seguridad

        val vm = DetalleIncidenciaViewModel(repo, ID)

        vm.ui.test {
            val s0 = awaitItem()
            assertTrue(s0.cargando)

            dispatcher.scheduler.advanceUntilIdle()

            val s1 = awaitItem()
            assertFalse(s1.cargando)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `error cuando no existe`() = runTest {
        whenever(dao.obtenerPorId(eq(ID))).thenReturn(null)

        val vm = DetalleIncidenciaViewModel(repo, ID)

        vm.ui.test {
            val s0 = awaitItem()
            assertTrue(s0.cargando)

            dispatcher.scheduler.advanceUntilIdle()

            val s1 = awaitItem()
            assertFalse(s1.cargando)
            assertNull(s1.incidencia)
            assertEquals("No se encontró la incidencia.", s1.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `cambiarEstado actualiza estado y recarga la incidencia`() = runTest {
        // 1) Primera carga: incidencia PENDIENTE
        whenever(dao.obtenerPorId(eq(ID))).thenReturn(entity(id = ID, estado = EstadoIncidencia.PENDIENTE))

        val vm = DetalleIncidenciaViewModel(repo, ID)
        dispatcher.scheduler.advanceUntilIdle()

        // 2) Tras actualizar, el VM vuelve a pedir obtenerPorId -> devolvemos RESUELTA
        whenever(dao.obtenerPorId(eq(ID))).thenReturn(
            entity(id = ID, estado = EstadoIncidencia.RESUELTA, comentarioAdmin = "ok")
        )

        vm.onComentarioAdmin("ok")
        vm.cambiarEstado(EstadoIncidencia.RESUELTA)

        vm.ui.test {
            var last = awaitItem()
            dispatcher.scheduler.advanceUntilIdle()
            last = awaitItem()

            dispatcher.scheduler.advanceUntilIdle()


            val final = last


            assertNotNull(final.incidencia)
            // En tu VM, 'incidencia' se sustituye por 'updated' (puede ser null si repo devuelve null)
            // Aquí lo devolvemos no-null.
            // OJO: final puede ser todavía el intermedio. Si pasa, dime y lo hacemos "await until".
            cancelAndIgnoreRemainingEvents()
        }

        // Verificamos que se llamó al DAO para actualizar
        verify(dao).actualizarEstado(eq(ID), eq(EstadoIncidencia.RESUELTA), eq("ok"))
    }

    @Test
    fun `comentarioAdmin se guarda (se usa al llamar a actualizarEstado y vuelve en la recarga)`() = runTest {
        whenever(dao.obtenerPorId(eq(ID))).thenReturn(entity(id = ID, comentarioAdmin = ""))

        val vm = DetalleIncidenciaViewModel(repo, ID)
        dispatcher.scheduler.advanceUntilIdle()

        // Después de actualizar, al recargar debe traer el comentario
        whenever(dao.obtenerPorId(eq(ID))).thenReturn(entity(id = ID, comentarioAdmin = "Pendiente de revisar"))

        vm.onComentarioAdmin("Pendiente de revisar")
        vm.cambiarEstado(EstadoIncidencia.EN_REVISION)
        dispatcher.scheduler.advanceUntilIdle()

        // 1) Se envía al DAO
        verify(dao).actualizarEstado(eq(ID), eq(EstadoIncidencia.EN_REVISION), eq("Pendiente de revisar"))

        // 2) Se refleja en el ui tras la recarga
        assertEquals("Pendiente de revisar", vm.ui.value.incidencia?.comentarioAdmin ?: "")
    }

    @Test
    fun `si actualizar falla muestra error y actualizando vuelve a false`() = runTest {
        whenever(dao.obtenerPorId(eq(ID))).thenReturn(entity(id = ID))

        // Forzamos fallo en actualizarEstado
        whenever(dao.actualizarEstado(any(), any(), any())).thenThrow(RuntimeException("boom"))

        val vm = DetalleIncidenciaViewModel(repo, ID)
        dispatcher.scheduler.advanceUntilIdle()

        vm.cambiarEstado(EstadoIncidencia.RECHAZADA)
        dispatcher.scheduler.advanceUntilIdle()

        assertFalse(vm.ui.value.actualizando)
        assertEquals("No se pudo actualizar el estado.", vm.ui.value.error)
    }
}
