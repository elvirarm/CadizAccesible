package com.example.cadizaccesible.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cadizaccesible.data.db.entities.IncidenciaEntity
import com.example.cadizaccesible.data.reports.EstadoIncidencia
import com.example.cadizaccesible.data.reports.Gravedad
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz de acceso a datos (DAO) para las incidencias.
 * Proporciona métodos para interactuar con la tabla de incidencias en la base de datos.
 */
@Dao
interface IncidenciaDao {

    /**
     * Inserta una nueva incidencia en la base de datos.
     * Si la incidencia ya existe, se reemplaza.
     * @param incidencia La incidencia a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(incidencia: IncidenciaEntity)

    /**
     * Obtiene todas las incidencias de la base de datos, ordenadas por fecha descendente.
     * @return Un [Flow] que emite una lista de [IncidenciaEntity].
     */
    @Query("SELECT * FROM incidencias ORDER BY fechaEpochMs DESC")
    fun obtenerTodas(): Flow<List<IncidenciaEntity>>

    /**
     * Obtiene todas las incidencias creadas por un usuario específico.
     * @param email El email del usuario.
     * @return Un [Flow] que emite una lista de [IncidenciaEntity].
     */
    @Query("SELECT * FROM incidencias WHERE emailCreador = :email ORDER BY fechaEpochMs DESC")
    fun obtenerPorCreador(email: String): Flow<List<IncidenciaEntity>>

    /**
     * Obtiene una incidencia por su ID.
     * @param id El ID de la incidencia.
     * @return La [IncidenciaEntity] correspondiente, o null si no se encuentra.
     */
    @Query("SELECT * FROM incidencias WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: String): IncidenciaEntity?

    /**
     * Elimina una incidencia de la base de datos por su ID.
     * @param id El ID de la incidencia a eliminar.
     */
    @Query("DELETE FROM incidencias WHERE id = :id")
    suspend fun eliminar(id: String)

    /**
     * Actualiza el estado de una incidencia.
     * @param id El ID de la incidencia a actualizar.
     * @param estado El nuevo estado de la incidencia.
     * @param comentario El comentario del administrador.
     */
    @Query("UPDATE incidencias SET estado = :estado, comentarioAdmin = :comentario WHERE id = :id")
    suspend fun actualizarEstado(id: String, estado: EstadoIncidencia, comentario: String)

    /**
     * Obtiene el número total de incidencias.
     * @return Un [Flow] que emite el número total de incidencias.
     */
    @Query("SELECT COUNT(*) FROM incidencias")
    fun totalIncidencias(): Flow<Int>

    /**
     * Obtiene el número total de incidencias para un estado específico.
     * @param estado El estado de la incidencia.
     * @return Un [Flow] que emite el número total de incidencias para el estado dado.
     */
    @Query("SELECT COUNT(*) FROM incidencias WHERE estado = :estado")
    fun totalPorEstado(estado: EstadoIncidencia): Flow<Int>

    /**
     * Obtiene el número total de incidencias para una gravedad específica.
     * @param gravedad La gravedad de la incidencia.
     * @return Un [Flow] que emite el número total de incidencias para la gravedad dada.
     */
    @Query("SELECT COUNT(*) FROM incidencias WHERE gravedad = :gravedad")
    fun totalPorGravedad(gravedad: Gravedad): Flow<Int>

    /**
     * Obtiene el número total de incidencias urgentes.
     * @return Un [Flow] que emite el número total de incidencias urgentes.
     */
    @Query("SELECT COUNT(*) FROM incidencias WHERE esUrgente = 1")
    fun totalUrgentes(): Flow<Int>

    /**
     * Cuenta el número total de incidencias.
     * @return El número total de incidencias.
     */
    @Query("SELECT COUNT(*) FROM incidencias")
    suspend fun contar(): Int

    /**
     * Obtiene la distribución de incidencias por estado.
     * @return Un [Flow] que emite una lista de [com.example.cadizaccesible.data.reports.ConteoEstado].
     */
    @Query("SELECT estado AS estado, COUNT(*) AS total FROM incidencias GROUP BY estado")
    fun distribucionPorEstado(): Flow<List<com.example.cadizaccesible.data.reports.ConteoEstado>>

    /**
     * Obtiene la distribución de incidencias por gravedad.
     * @return Un [Flow] que emite una lista de [com.example.cadizaccesible.data.reports.ConteoGravedad].
     */
    @Query("SELECT gravedad AS gravedad, COUNT(*) AS total FROM incidencias GROUP BY gravedad")
    fun distribucionPorGravedad(): Flow<List<com.example.cadizaccesible.data.reports.ConteoGravedad>>
}
