package com.example.cadizaccesible.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cadizaccesible.data.users.RolUsuario

/**
 * Representa un usuario en la base de datos.
 *
 * @property email El email del usuario, que también es la clave primaria.
 * @property nombre El nombre del usuario.
 * @property password La contraseña del usuario.
 * @property rol El rol del usuario (por ejemplo, normal o administrador).
 */
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val email: String,
    val nombre: String,
    val password: String,
    val rol: RolUsuario
)
