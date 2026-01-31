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

    suspend fun actualizarEstado(
        id: String,
        nuevoEstado: EstadoIncidencia,
        comentarioAdmin: String = ""
    ) {
        dao.actualizarEstado(id, nuevoEstado, comentarioAdmin)
    }


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
}
