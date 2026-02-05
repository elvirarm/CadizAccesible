package com.example.cadizaccesible.ui.screens.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cadizaccesible.data.sesion.GestorSesion
import com.example.cadizaccesible.data.users.RepositorioUsuariosRoom
import com.example.cadizaccesible.data.users.RolUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Representa el estado de la interfaz de usuario para los procesos de autenticación.
 * @property cargando Indica si hay una operación asíncrona en curso (login/registro).
 * @property error Almacena el mensaje descriptivo en caso de fallo en la operación.
 */
data class EstadoUiAuth(
    val cargando: Boolean = false,
    val error: String = ""
)

/**
 * ViewModel encargado de gestionar la lógica de negocio para el acceso y registro de usuarios.
 * * Utiliza [AndroidViewModel] para acceder al contexto de la aplicación y facilitar la
 * inicialización de los repositorios y gestores de sesión.
 */
class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val repositorio = RepositorioUsuariosRoom(app)
    private val gestorSesion = GestorSesion(app)

    // Estado interno mutable
    private val _estadoUi = MutableStateFlow(EstadoUiAuth())
    /** Flujo de estado público y de solo lectura para ser recolectado por la UI (Compose). */
    val estadoUi = _estadoUi.asStateFlow()

    /**
     * Intenta autenticar a un usuario con sus credenciales.
     * * Si el éxito es confirmado, guarda la sesión de forma persistente en [GestorSesion]
     * y dispara el callback [alEntrar] con el rol del usuario obtenido.
     */
    fun login(email: String, contrasena: String, alEntrar: (RolUsuario) -> Unit) {
        _estadoUi.value = EstadoUiAuth(cargando = true)

        viewModelScope.launch {
            runCatching {
                repositorio.login(email.trim(), contrasena)
            }.onSuccess { usuario ->
                if (usuario != null) {
                    gestorSesion.guardarSesion(usuario.email, usuario.rol)
                    _estadoUi.value = EstadoUiAuth()
                    alEntrar(usuario.rol)
                } else {
                    _estadoUi.value = EstadoUiAuth(error = "Credenciales incorrectas.")
                }
            }.onFailure { e ->
                _estadoUi.value = EstadoUiAuth(error = e.message ?: "Error inesperado")
            }
        }
    }

    /**
     * Registra un nuevo usuario en la base de datos local.
     * * Incluye una lógica de validación de código administrativo: si el usuario intenta
     * registrarse como [RolUsuario.ADMIN] pero el código es incorrecto, se le asigna
     * forzosamente el rol de [RolUsuario.CIUDADANO].
     */
    fun registrar(
        nombre: String,
        email: String,
        contrasena: String,
        rolElegido: RolUsuario,
        codigoAdmin: String,
        alRegistrar: () -> Unit
    ) {
        _estadoUi.value = EstadoUiAuth(cargando = true)

        // Validación simple de seguridad para el rol de administrador
        val rolFinal = if (rolElegido == RolUsuario.ADMIN && codigoAdmin != "CADIZ-ADMIN") {
            RolUsuario.CIUDADANO
        } else rolElegido

        viewModelScope.launch {
            val resultado = repositorio.registrar(
                nombre = nombre.trim(),
                email = email.trim(),
                password = contrasena,
                rol = rolFinal
            )

            resultado.onSuccess {
                _estadoUi.value = EstadoUiAuth()
                alRegistrar()
            }.onFailure { e ->
                _estadoUi.value = EstadoUiAuth(error = e.message ?: "Error inesperado")
            }
        }
    }

    /** Elimina cualquier mensaje de error presente en el estado de la UI. */
    fun limpiarError() {
        if (_estadoUi.value.error.isNotBlank()) {
            _estadoUi.value = _estadoUi.value.copy(error = "")
        }
    }
}