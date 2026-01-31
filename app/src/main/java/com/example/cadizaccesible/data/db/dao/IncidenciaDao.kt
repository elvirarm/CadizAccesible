package com.example.cadizaccesible.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cadizaccesible.data.db.entities.IncidenciaEntity
import com.example.cadizaccesible.data.reports.EstadoIncidencia
import com.example.cadizaccesible.data.reports.Gravedad
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidenciaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(incidencia: IncidenciaEntity)

    @Query("SELECT * FROM incidencias ORDER BY fechaEpochMs DESC")
    fun obtenerTodas(): Flow<List<IncidenciaEntity>>

    @Query("SELECT * FROM incidencias WHERE emailCreador = :email ORDER BY fechaEpochMs DESC")
    fun obtenerPorCreador(email: String): Flow<List<IncidenciaEntity>>

    @Query("SELECT * FROM incidencias WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: String): IncidenciaEntity?

    @Query("DELETE FROM incidencias WHERE id = :id")
    suspend fun eliminar(id: String)

    @Query("UPDATE incidencias SET estado = :estado, comentarioAdmin = :comentario WHERE id = :id")
    suspend fun actualizarEstado(id: String, estado: EstadoIncidencia, comentario: String)

    @Query("SELECT COUNT(*) FROM incidencias")
    fun totalIncidencias(): Flow<Int>

    @Query("SELECT COUNT(*) FROM incidencias WHERE estado = :estado")
    fun totalPorEstado(estado: EstadoIncidencia): Flow<Int>

    @Query("SELECT COUNT(*) FROM incidencias WHERE gravedad = :gravedad")
    fun totalPorGravedad(gravedad: Gravedad): Flow<Int>

    @Query("SELECT COUNT(*) FROM incidencias WHERE esUrgente = 1")
    fun totalUrgentes(): Flow<Int>

    @Query("SELECT COUNT(*) FROM incidencias")
    suspend fun contar(): Int

    @Query("SELECT estado AS estado, COUNT(*) AS total FROM incidencias GROUP BY estado")
    fun distribucionPorEstado(): Flow<List<com.example.cadizaccesible.data.reports.ConteoEstado>>

    @Query("SELECT gravedad AS gravedad, COUNT(*) AS total FROM incidencias GROUP BY gravedad")
    fun distribucionPorGravedad(): Flow<List<com.example.cadizaccesible.data.reports.ConteoGravedad>>
}
