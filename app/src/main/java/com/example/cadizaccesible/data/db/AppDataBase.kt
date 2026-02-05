package com.example.cadizaccesible.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cadizaccesible.data.db.dao.IncidenciaDao
import com.example.cadizaccesible.data.db.dao.UsuarioDao
import com.example.cadizaccesible.data.db.entities.IncidenciaEntity
import com.example.cadizaccesible.data.db.entities.UsuarioEntity

/**
 * Base de datos principal de la aplicación "Cádiz Accesible".
 * * Esta clase abstracta define la configuración de Room y sirve como el punto de acceso principal
 * a los datos persistidos de la aplicación.
 * * @property entities Lista de clases de entidad asociadas a la base de datos (Usuario e Incidencia).
 * @property version Versión del esquema de la base de datos. Debe incrementarse al realizar migraciones.
 * @property exportSchema Indica si se debe exportar el esquema a un archivo JSON (deshabilitado aquí).
 */
@Database(
    entities = [UsuarioEntity::class, IncidenciaEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    /** Proporciona el objeto de acceso a datos (DAO) para la tabla de Usuarios. */
    abstract fun usuarioDao(): UsuarioDao

    /** Proporciona el objeto de acceso a datos (DAO) para la tabla de Incidencias. */
    abstract fun incidenciaDao(): IncidenciaDao

    companion object {
        /**
         * Referencia a la instancia única de la base de datos.
         * Se marca como [Volatile] para asegurar que los cambios realizados por un hilo
         * sean visibles inmediatamente para todos los demás hilos.
         */
        @Volatile
        private var instancia: AppDatabase? = null

        /**
         * Obtiene la instancia única (Singleton) de [AppDatabase].
         * * Si la instancia ya existe, la devuelve inmediatamente. Si no, crea una nueva
         * base de datos de forma segura para hilos (thread-safe) usando un bloque synchronized.
         * * @param contexto El contexto de la aplicación para inicializar la base de datos.
         * @return La instancia global de la base de datos de la aplicación.
         */
        fun obtener(contexto: Context): AppDatabase {
            // Si instancia no es nula, la devuelve. Si es nula, entra al bloque sincronizado.
            return instancia ?: synchronized(this) {
                // Doble comprobación: crea la DB solo si sigue siendo nula tras obtener el lock.
                val db = Room.databaseBuilder(
                    contexto.applicationContext,
                    AppDatabase::class.java,
                    "cadizaccesible.db"
                ).build()
                instancia = db
                db
            }
        }
    }
}