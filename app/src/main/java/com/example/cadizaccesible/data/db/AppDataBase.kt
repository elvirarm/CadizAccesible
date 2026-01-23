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

@Database(
    entities = [UsuarioEntity::class, IncidenciaEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun incidenciaDao(): IncidenciaDao

    companion object {
        @Volatile private var instancia: AppDatabase? = null

        fun obtener(contexto: Context): AppDatabase {
            return instancia ?: synchronized(this) {
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
