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

data class EstadoUiAuth(
    val cargando: Boolean = false,
    val error: String = ""
)

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val repositorio = RepositorioUsuariosRoom(app)
    private val gestorSesion = GestorSesion(app)

    private val _estadoUi = MutableStateFlow(EstadoUiAuth())
    val estadoUi = _estadoUi.asStateFlow()

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

    fun registrar(
        nombre: String,
        email: String,
        contrasena: String,
        rolElegido: RolUsuario,
        codigoAdmin: String,
        alRegistrar: () -> Unit
    ) {
        _estadoUi.value = EstadoUiAuth(cargando = true)

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

    fun limpiarError() {
        if (_estadoUi.value.error.isNotBlank()) {
            _estadoUi.value = _estadoUi.value.copy(error = "")
        }
    }
}
