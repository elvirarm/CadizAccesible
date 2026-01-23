package com.example.cadizaccesible.data.users

import android.content.Context
import com.example.cadizaccesible.data.db.AppDatabase
import com.example.cadizaccesible.data.db.entities.UsuarioEntity

class RepositorioUsuariosRoom(contexto: Context) {

    private val usuarioDao = AppDatabase.obtener(contexto).usuarioDao()

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

    suspend fun login(email: String, password: String): UsuarioEntity? {
        return usuarioDao.validarLogin(email, password)
    }
}

