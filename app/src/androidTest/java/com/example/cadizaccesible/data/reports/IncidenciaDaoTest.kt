package com.example.cadizaccesible.data.reports

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cadizaccesible.data.db.AppDatabase
import com.example.cadizaccesible.data.db.dao.IncidenciaDao
import com.example.cadizaccesible.data.db.entities.IncidenciaEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IncidenciaDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: IncidenciaDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        dao = db.incidenciaDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun entityEjemplo(
        id: String,
        email: String = "ciudadano@hotmail.com",
        estado: EstadoIncidencia = EstadoIncidencia.PENDIENTE,
        gravedad: Gravedad = Gravedad.MEDIA,
        urgente: Boolean = false,
        temporal: Boolean = false,
        fechaEpochMs: Long = System.currentTimeMillis()
    ): IncidenciaEntity {
        return IncidenciaEntity(
            id = id,
            emailCreador = email,
            titulo = "Rampa rota",
            descripcion = "No se puede pasar con silla de ruedas",
            categoria = "Aceras",
            accesibilidadAfectada = "Movilidad",
            gravedad = gravedad,
            esUrgente = urgente,
            esObstaculoTemporal = temporal,
            direccionTexto = "Calle Juan Lorenzo, 14",
            latitud = 36.5297,
            longitud = -6.2920,
            fotoUri = null,
            estado = estado,
            comentarioAdmin = "",
            fechaEpochMs = fechaEpochMs
        )
    }

    @Test
    fun insertar_y_obtenerPorId_devuelve_entity() = runBlocking {
        dao.insertar(entityEjemplo(id = "A1"))

        val entity = dao.obtenerPorId("A1")
        assertNotNull(entity)
        assertEquals("A1", entity?.id)
        assertEquals("Rampa rota", entity?.titulo)
        assertEquals("ciudadano@hotmail.com", entity?.emailCreador)
    }

    @Test
    fun obtenerTodas_emite_lista_con_elementos() = runBlocking {
        dao.insertar(entityEjemplo(id = "1", fechaEpochMs = 1))
        dao.insertar(entityEjemplo(id = "2", fechaEpochMs = 2))

        val lista = dao.obtenerTodas().first()
        assertEquals(2, lista.size)
    }

    @Test
    fun eliminar_borra_incidencia() = runBlocking {
        dao.insertar(entityEjemplo(id = "B1"))

        dao.eliminar("B1")

        val entity = dao.obtenerPorId("B1")
        assertNull(entity)
    }

    @Test
    fun obtenerPorCreador_filtra_por_email() = runBlocking {
        dao.insertar(entityEjemplo(id = "1", email = "a@a.com"))
        dao.insertar(entityEjemplo(id = "2", email = "b@b.com"))
        dao.insertar(entityEjemplo(id = "3", email = "a@a.com"))

        val listaA = dao.obtenerPorCreador("a@a.com").first()
        assertEquals(2, listaA.size)
        assertTrue(listaA.all { it.emailCreador.equals("a@a.com", ignoreCase = true) })
    }

    @Test
    fun actualizarEstado_actualiza_estado_y_comentario() = runBlocking {
        dao.insertar(entityEjemplo(id = "C1", estado = EstadoIncidencia.PENDIENTE))

        dao.actualizarEstado(
            id = "C1",
            estado = EstadoIncidencia.EN_REVISION,
            comentario = "Revisando"
        )

        val entity = dao.obtenerPorId("C1")
        assertNotNull(entity)
        assertEquals(EstadoIncidencia.EN_REVISION, entity?.estado)
        assertEquals("Revisando", entity?.comentarioAdmin)
    }

    @Test
    fun contar_devuelve_total_correcto() = runBlocking {
        dao.insertar(entityEjemplo(id = "1"))
        dao.insertar(entityEjemplo(id = "2"))
        dao.insertar(entityEjemplo(id = "3"))

        val total = dao.contar()
        assertEquals(3, total)
    }

    @Test
    fun totalPorEstado_emite_valor_correcto() = runBlocking {
        dao.insertar(entityEjemplo(id = "1", estado = EstadoIncidencia.PENDIENTE))
        dao.insertar(entityEjemplo(id = "2", estado = EstadoIncidencia.RECHAZADA))
        dao.insertar(entityEjemplo(id = "3", estado = EstadoIncidencia.RECHAZADA))

        val pendientes = dao.totalPorEstado(EstadoIncidencia.PENDIENTE).first()
        val rechazadas = dao.totalPorEstado(EstadoIncidencia.RECHAZADA).first()

        assertEquals(1, pendientes)
        assertEquals(2, rechazadas)
    }

    @Test
    fun totalUrgentes_emite_valor_correcto() = runBlocking {
        dao.insertar(entityEjemplo(id = "1", urgente = true))
        dao.insertar(entityEjemplo(id = "2", urgente = false))
        dao.insertar(entityEjemplo(id = "3", urgente = true))

        val urgentes = dao.totalUrgentes().first()
        assertEquals(2, urgentes)
    }

    @Test
    fun distribucionPorEstado_emite_conteos() = runBlocking {
        dao.insertar(entityEjemplo(id = "1", estado = EstadoIncidencia.PENDIENTE))
        dao.insertar(entityEjemplo(id = "2", estado = EstadoIncidencia.EN_REVISION))
        dao.insertar(entityEjemplo(id = "3", estado = EstadoIncidencia.EN_REVISION))

        val dist = dao.distribucionPorEstado().first()

        val enRev = dist.firstOrNull { it.estado == EstadoIncidencia.EN_REVISION }?.total ?: 0
        val pend = dist.firstOrNull { it.estado == EstadoIncidencia.PENDIENTE }?.total ?: 0

        assertEquals(2, enRev)
        assertEquals(1, pend)
    }

    @Test
    fun distribucionPorGravedad_emite_conteos() = runBlocking {
        dao.insertar(entityEjemplo(id = "1", gravedad = Gravedad.BAJA))
        dao.insertar(entityEjemplo(id = "2", gravedad = Gravedad.MEDIA))
        dao.insertar(entityEjemplo(id = "3", gravedad = Gravedad.MEDIA))
        dao.insertar(entityEjemplo(id = "4", gravedad = Gravedad.ALTA))

        val dist = dao.distribucionPorGravedad().first()

        val baja = dist.firstOrNull { it.gravedad == Gravedad.BAJA }?.total ?: 0
        val media = dist.firstOrNull { it.gravedad == Gravedad.MEDIA }?.total ?: 0
        val alta = dist.firstOrNull { it.gravedad == Gravedad.ALTA }?.total ?: 0

        assertEquals(1, baja)
        assertEquals(2, media)
        assertEquals(1, alta)
    }
}
