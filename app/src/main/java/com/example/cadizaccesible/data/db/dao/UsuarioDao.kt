package com.example.cadizaccesible.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cadizaccesible.data.db.entities.UsuarioEntity

/**
 * Interfaz de acceso a datos (DAO) para los usuarios.
 * Proporciona métodos para interactuar con la tabla de usuarios en la base de datos.
 */
@Dao
interface UsuarioDao {

    /**
     * Inserta un nuevo usuario en la base de datos.
     * Si el usuario ya existe (mismo email), la operación se aborta.
     * @param usuario El usuario a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: UsuarioEntity)

    /**
     * Obtiene un usuario por su dirección de email.
     * @param email El email del usuario a buscar.
     * @return La [UsuarioEntity] correspondiente si se encuentra, o null en caso contrario.
     */
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun obtenerPorEmail(email: String): UsuarioEntity?

    /**
     * Valida las credenciales de un usuario para el inicio de sesión.
     * @param email El email del usuario.
     * @param password La contraseña del usuario.
     * @return La [UsuarioEntity] si las credenciales son correctas, o null en caso contrario.
     */
    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password LIMIT 1")
    suspend fun validarLogin(email: String, password: String): UsuarioEntity?
}
