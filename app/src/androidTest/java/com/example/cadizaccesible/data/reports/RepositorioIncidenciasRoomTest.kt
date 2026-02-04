package com.example.cadizaccesible.data.reports

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cadizaccesible.data.db.AppDatabase
import com.example.cadizaccesible.data.db.entities.IncidenciaEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositorioIncidenciasRoomTest {

    private lateinit var db: AppDatabase
    private lateinit var repo: RepositorioIncidenciasRoom

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        repo = RepositorioIncidenciasRoom(db.incidenciaDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    /**
     * Helper: insertamos directamente Entity para controlar el ID.
     * (crearIncidencia genera UUID y no podemos predecirlo en assertions)
     */
    private suspend fun insertarEntity(
        id: String,
        email: String = "a@a.com",
        estado: EstadoIncidencia = EstadoIncidencia.PENDIENTE,
        gravedad: Gravedad = Gravedad.MEDIA,
        urgente: Boolean = false
    ) {
        val e = IncidenciaEntity(
            id = id,
            emailCreador = email,
            titulo = "Rampa rota",
            descripcion = "No accesible",
            categoria = "Aceras",
            accesibilidadAfectada = "Movilidad",
            gravedad = gravedad,
            esUrgente = urgente,
            esObstaculoTemporal = false,
            direccionTexto = "Calle Ancha",
            latitud = 36.5297,
            longitud = -6.2920,
            fotoUri = null,
            estado = estado,
            comentarioAdmin = "",
            fechaEpochMs = System.currentTimeMillis()
        )
        db.incidenciaDao().insertar(e)
    }

    @Test
    fun obtenerTodas_devuelve_incidencias_convertidas_a_modelo() = runBlocking {
        insertarEntity("1")
        insertarEntity("2")

        val lista = repo.obtenerTodas().first()

        assertEquals(2, lista.size)
        assertTrue(lista.all { it.titulo == "Rampa rota" })
    }

    @Test
    fun obtenerPorCreador_filtra_correctamente() = runBlocking {
        insertarEntity("1", email = "a@a.com")
        insertarEntity("2", email = "b@b.com")
        insertarEntity("3", email = "a@a.com")

        val listaA = repo.obtenerPorCreador("a@a.com").first()

        assertEquals(2, listaA.size)
        assertTrue(listaA.all { it.emailCreador.equals("a@a.com", ignoreCase = true) })
    }

    @Test
    fun obtenerPorId_devuelve_modelo() = runBlocking {
        insertarEntity("X1", email = "x@x.com")

        val inc = repo.obtenerPorId("X1")

        assertNotNull(inc)
        assertEquals("X1", inc?.id)
        assertEquals("x@x.com", inc?.emailCreador)
    }

    @Test
    fun eliminarIncidencia_elimina() = runBlocking {
        insertarEntity("DEL1")

        repo.eliminarIncidencia("DEL1")

        val lista = repo.obtenerTodas().first()
        assertTrue(lista.none { it.id == "DEL1" })
    }

    @Test
    fun actualizarEstado_actualiza_estado_y_comentario() = runBlocking {
        insertarEntity("UP1", estado = EstadoIncidencia.PENDIENTE)

        repo.actualizarEstado(
            id = "UP1",
            nuevoEstado = EstadoIncidencia.EN_REVISION,
            comentarioAdmin = "Marcada en revisión"
        )

        val inc = repo.obtenerPorId("UP1")
        assertNotNull(inc)
        assertEquals(EstadoIncidencia.EN_REVISION, inc?.estado)
        assertEquals("Marcada en revisión", inc?.comentarioAdmin)
    }

    @Test
    fun totalUrgentes_refleja_la_db() = runBlocking {
        insertarEntity("1", urgente = true)
        insertarEntity("2", urgente = false)
        insertarEntity("3", urgente = true)

        val urgentes = repo.totalUrgentes().first()
        assertEquals(2, urgentes)
    }

    @Test
    fun distribucionPorEstado_emite_conteos() = runBlocking {
        insertarEntity("1", estado = EstadoIncidencia.PENDIENTE)
        insertarEntity("2", estado = EstadoIncidencia.EN_REVISION)
        insertarEntity("3", estado = EstadoIncidencia.EN_REVISION)

        val dist = repo.distribucionPorEstado().first()

        val enRev = dist.firstOrNull { it.estado == EstadoIncidencia.EN_REVISION }?.total ?: 0
        val pend = dist.firstOrNull { it.estado == EstadoIncidencia.PENDIENTE }?.total ?: 0

        assertEquals(2, enRev)
        assertEquals(1, pend)
    }

    @Test
    fun distribucionPorGravedad_emite_conteos() = runBlocking {
        insertarEntity("1", gravedad = Gravedad.BAJA)
        insertarEntity("2", gravedad = Gravedad.MEDIA)
        insertarEntity("3", gravedad = Gravedad.MEDIA)
        insertarEntity("4", gravedad = Gravedad.ALTA)

        val dist = repo.distribucionPorGravedad().first()

        val baja = dist.firstOrNull { it.gravedad == Gravedad.BAJA }?.total ?: 0
        val media = dist.firstOrNull { it.gravedad == Gravedad.MEDIA }?.total ?: 0
        val alta = dist.firstOrNull { it.gravedad == Gravedad.ALTA }?.total ?: 0

        assertEquals(1, baja)
        assertEquals(2, media)
        assertEquals(1, alta)
    }
}
