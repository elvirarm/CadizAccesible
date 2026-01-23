package com.example.cadizaccesible.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cadizaccesible.data.users.RolUsuario

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val email: String,
    val nombre: String,
    val password: String,
    val rol: RolUsuario
)
