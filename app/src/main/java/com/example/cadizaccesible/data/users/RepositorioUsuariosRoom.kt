package com.example.cadizaccesible.data.users

import android.content.Context
import com.example.cadizaccesible.data.db.AppDatabase
import com.example.cadizaccesible.data.db.entities.UsuarioEntity

/**
 * Repositorio encargado de gestionar la persistencia de usuarios en la base de datos local.
 * * Proporciona una capa de abstracción sobre [UsuarioDao] para realizar operaciones de
 * registro y autenticación de forma segura.
 * * @param contexto Contexto de la aplicación necesario para inicializar la base de datos Room.
 */
class RepositorioUsuariosRoom(contexto: Context) {

    /** Instancia del DAO de usuarios obtenida a través de la base de datos única. */
    private val usuarioDao = AppDatabase.obtener(contexto).usuarioDao()

    /**
     * Registra un nuevo usuario en el sistema.
     * * Utiliza [runCatching] para encapsular la operación de inserción. Esto permite capturar
     * excepciones de base de datos (como una violación de clave única si el email ya existe)
     * y devolver un objeto [Result].
     * * @param nombre Nombre completo o alias del usuario.
     * @param email Correo electrónico único (servirá como identificador).
     * @param password Contraseña del usuario (se recomienda que llegue ya cifrada).
     * @param rol Nivel de privilegios asignado ([RolUsuario]).
     * @return Un [Result] que contiene la [UsuarioEntity] creada en caso de éxito, o la excepción en caso de error.
     */
    suspend fun registrar(
        nombre: String,
        email: String,
        password: String,
        rol: RolUsuario
    ): Result<UsuarioEntity> {
        return runCatching {
            val usuario = UsuarioEntity(
                email = email,
                nombre = nombre,
                password = password,
                rol = rol
            )
            usuarioDao.insertar(usuario)
            usuario
        }
    }

    /**
     * Valida las credenciales de un usuario para el inicio de sesión.
     * * Consulta en la base de datos si existe una coincidencia exacta para el email y la contraseña.
     * * @param email Correo electrónico introducido.
     * @param password Contraseña introducida.
     * @return El objeto [UsuarioEntity] si las credenciales son válidas, o `null` si no hay coincidencia.
     */
    suspend fun login(email: String, password: String): UsuarioEntity? {
        return usuarioDao.validarLogin(email, password)
    }
}