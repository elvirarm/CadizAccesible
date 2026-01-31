package com.example.cadizaccesible.data.users

data class UsuarioApp(
    val nombre: String,
    val email: String,
    val contraseña: String,
    val rol: RolUsuario
)

object RepositorioUsuarios {

    private val usuarios = mutableListOf(
        UsuarioApp(
            nombre = "Admin Cádiz",
            email = "admin@cadiz.es",
            contraseña = "admin1234",
            rol = RolUsuario.ADMIN
        )
    )

    fun registrar(nombre: String, email: String, contraseña: String, rol: RolUsuario): Result<Unit> {
        if (usuarios.any { it.email.equals(email, ignoreCase = true) }) {
            return Result.failure(IllegalArgumentException("Ese email ya está registrado."))
        }
        usuarios.add(UsuarioApp(nombre, email, contraseña, rol))
        return Result.success(Unit)
    }

    fun iniciarSesion(email: String, contraseña: String): Result<UsuarioApp> {
        val usuario = usuarios.firstOrNull {
            it.email.equals(email, ignoreCase = true) && it.contraseña == contraseña
        } ?: return Result.failure(IllegalArgumentException("Credenciales incorrectas."))

        return Result.success(usuario)
    }
}
