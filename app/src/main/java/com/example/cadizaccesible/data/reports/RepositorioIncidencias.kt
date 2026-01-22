package com.example.cadizaccesible.data.reports

import com.example.cadizaccesible.data.reports.EstadoIncidencia
import com.example.cadizaccesible.data.reports.Gravedad
import com.example.cadizaccesible.data.reports.Incidencia
import java.util.UUID

object RepositorioIncidencias {

    private val incidencias = mutableListOf<Incidencia>()

    fun crearIncidencia(
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
    ): Incidencia {
        val nuevo = Incidencia(
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
            fotoUri = fotoUri
        )
        incidencias.add(nuevo)
        return nuevo
    }

    fun obtenerTodas(): List<Incidencia> =
        incidencias.sortedByDescending { it.fechaEpochMs }

    fun obtenerPorCreador(email: String): List<Incidencia> =
        incidencias
            .filter { it.emailCreador.equals(email, ignoreCase = true) }
            .sortedByDescending { it.fechaEpochMs }

    fun obtenerPorId(id: String): Incidencia? =
        incidencias.firstOrNull { it.id == id }

    fun actualizarEstado(
        id: String,
        nuevoEstado: EstadoIncidencia,
        comentarioAdmin: String = ""
    ): Boolean {
        val idx = incidencias.indexOfFirst { it.id == id }
        if (idx == -1) return false

        val actual = incidencias[idx]
        incidencias[idx] = actual.copy(
            estado = nuevoEstado,
            comentarioAdmin = comentarioAdmin
        )
        return true
    }

    fun precargarDemoSiVacio() {
        if (incidencias.isNotEmpty()) return

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
