package com.example.cadizaccesible.data.reports

import android.content.Context
import com.example.cadizaccesible.data.db.AppDatabase
import com.example.cadizaccesible.data.db.dao.IncidenciaDao
import com.example.cadizaccesible.data.db.entities.IncidenciaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Implementación del repositorio de incidencias utilizando Room como persistencia local.
 * * Esta clase centraliza el acceso a las incidencias, realizando la conversión necesaria
 * entre las entidades de la base de datos ([IncidenciaEntity]) y los modelos de dominio ([Incidencia]).
 * * @property dao El objeto de acceso a datos para las operaciones de base de datos.
 */
class RepositorioIncidenciasRoom(
    private val dao: IncidenciaDao
) {
    /**
     * Constructor secundario que inicializa el DAO a partir del contexto de la aplicación.
     * @param contexto Contexto necesario para obtener la instancia de [AppDatabase].
     */
    constructor(contexto: Context) : this(
        AppDatabase.obtener(contexto).incidenciaDao()
    )

    /**
     * Recupera todas las incidencias de la base de datos de forma reactiva.
     * @return Un [Flow] que emite una lista de objetos [Incidencia] cada vez que hay cambios.
     */
    fun obtenerTodas(): Flow<List<Incidencia>> =
        dao.obtenerTodas().map { lista -> lista.map { it.aModelo() } }

    /**
     * Filtra las incidencias creadas por un usuario específico.
     * @param email Correo electrónico del creador.
     * @return [Flow] con la lista de incidencias filtradas.
     */
    fun obtenerPorCreador(email: String): Flow<List<Incidencia>> =
        dao.obtenerPorCreador(email).map { lista -> lista.map { it.aModelo() } }

    /**
     * Busca una incidencia específica por su identificador único.
     * @param id El UUID de la incidencia.
     * @return El objeto [Incidencia] si existe, o null en caso contrario.
     */
    suspend fun obtenerPorId(id: String): Incidencia? =
        dao.obtenerPorId(id)?.aModelo()

    /**
     * Crea y persiste una nueva incidencia en el sistema.
     * Genera automáticamente un [UUID] único y establece la fecha actual.
     */
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

    /** Elimina una incidencia de la base de datos permanentemente. */
    suspend fun eliminarIncidencia(id: String) {
        dao.eliminar(id)
    }

    /**
     * Actualiza el estado de una incidencia y añade un comentario administrativo opcional.
     * @param id ID de la incidencia.
     * @param nuevoEstado El nuevo [EstadoIncidencia] a asignar.
     * @param comentarioAdmin Explicación del cambio de estado por parte del administrador.
     */
    suspend fun actualizarEstado(
        id: String,
        nuevoEstado: EstadoIncidencia,
        comentarioAdmin: String = ""
    ) {
        dao.actualizarEstado(id, nuevoEstado, comentarioAdmin)
    }

    /**
     * Función de extensión privada para convertir una [IncidenciaEntity] de la DB
     * en un objeto [Incidencia] de la capa de dominio/UI.
     */
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

    // --- Métodos de Estadísticas y Conteos ---

    /** Obtiene un Flow con la lista de conteos agrupados por estado. */
    fun distribucionPorEstado() = dao.distribucionPorEstado()

    /** Obtiene un Flow con la lista de conteos agrupados por nivel de gravedad. */
    fun distribucionPorGravedad() = dao.distribucionPorGravedad()

    /** Devuelve el número total de incidencias registradas. */
    fun totalIncidencias() = dao.totalIncidencias()

    /** Devuelve el número de incidencias marcadas como urgentes. */
    fun totalUrgentes() = dao.totalUrgentes()

    /** Filtra el conteo total por un estado específico. */
    fun totalPorEstado(estado: EstadoIncidencia) = dao.totalPorEstado(estado)

    /** Filtra el conteo total por un nivel de gravedad específico. */
    fun totalPorGravedad(gravedad: Gravedad) = dao.totalPorGravedad(gravedad)
}