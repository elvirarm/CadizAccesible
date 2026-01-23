package com.example.cadizaccesible.data.reports

import android.content.Context
import com.example.cadizaccesible.data.db.AppDatabase
import com.example.cadizaccesible.data.db.entities.IncidenciaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class RepositorioIncidenciasRoom(contexto: Context) {

    private val dao = AppDatabase.obtener(contexto).incidenciaDao()

    fun obtenerTodas(): Flow<List<Incidencia>> =
        dao.obtenerTodas().map { lista -> lista.map { it.aModelo() } }

    fun obtenerPorCreador(email: String): Flow<List<Incidencia>> =
        dao.obtenerPorCreador(email).map { lista -> lista.map { it.aModelo() } }

    suspend fun obtenerPorId(id: String): Incidencia? =
        dao.obtenerPorId(id)?.aModelo()

    suspend fun crearIncidencia(
        emailCreador: String,
        titulo: String,
        descripcion: String,
        categoria: String,
        accesibilidadAfectada: String,
        gravedad: Gravedad,
        esUrgente: Boolean,
        esObstaculoTemporal: Boolean,
        direccionTexto: String,
        latitud: Double? = null,
        longitud: Double? = null,
        fotoUri: String? = null
    ) {
        val entidad = IncidenciaEntity(
            id = UUID.randomUUID().toString(),
            emailCreador = emailCreador,
            titulo = titulo,
            descripcion = descripcion,
            categoria = categoria,
            accesibilidadAfectada = accesibilidadAfectada,
            gravedad = gravedad,
            esUrgente = esUrgente,
            esObstaculoTemporal = esObstaculoTemporal,
            direccionTexto = direccionTexto,
            latitud = latitud,
            longitud = longitud,
            fotoUri = fotoUri,
            estado = EstadoIncidencia.PENDIENTE,
            comentarioAdmin = "",
            fechaEpochMs = System.currentTimeMillis()
        )
        dao.insertar(entidad)
    }

    suspend fun eliminarIncidencia(id: String) {
        dao.eliminar(id)
    }

    suspend fun actualizarEstado(id: String, nuevoEstado: EstadoIncidencia, comentarioAdmin: String = "") {
        dao.actualizarEstado(id, nuevoEstado, comentarioAdmin)
    }

    suspend fun precargarDemoSiVacio() {
        if (dao.contar() > 0) return

        crearIncidencia(
            emailCreador = "user@demo.com",
            titulo = "Coche en la acera bloqueando el paso",
            descripcion = "Un coche esta aparcado sobre la acera y no se puede pasar con silla de ruedas.",
            categoria = "Aceras",
            accesibilidadAfectada = "Movilidad",
            gravedad = Gravedad.ALTA,
            esUrgente = true,
            esObstaculoTemporal = true,
            direccionTexto = "Av. principal (cerca de un paso de peatones)"
        )

        crearIncidencia(
            emailCreador = "user@demo.com",
            titulo = "Semaforo sin aviso sonoro",
            descripcion = "No hay senal sonora para personas con discapacidad visual.",
            categoria = "Semaforos",
            accesibilidadAfectada = "Visual",
            gravedad = Gravedad.MEDIA,
            esUrgente = false,
            esObstaculoTemporal = false,
            direccionTexto = "Cruce centrico (referencia: farmacia)"
        )
    }
}

/** Mapeo Entity -> modelo que ya usas en UI */
private fun IncidenciaEntity.aModelo(): Incidencia = Incidencia(
    id = id,
    emailCreador = emailCreador,
    titulo = titulo,
    descripcion = descripcion,
    categoria = categoria,
    accesibilidadAfectada = accesibilidadAfectada,
    gravedad = gravedad,
    esUrgente = esUrgente,
    esObstaculoTemporal = esObstaculoTemporal,
    direccionTexto = direccionTexto,
    latitud = latitud,
    longitud = longitud,
    fotoUri = fotoUri,
    estado = estado,
    comentarioAdmin = comentarioAdmin,
    fechaEpochMs = fechaEpochMs
)
