package com.example.cadizaccesible.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cadizaccesible.data.sesion.GestorSesion
import com.example.cadizaccesible.data.users.RepositorioUsuariosRoom
import com.example.cadizaccesible.data.users.RolUsuario
import kotlinx.coroutines.launch

@Composable
fun PantallaLogin(
    irARegistro: () -> Unit,
    alLoguear: (RolUsuario) -> Unit,
    vm: AuthViewModel = viewModel()
) {

    LaunchedEffect(Unit) {
        vm.limpiarError()
    }

    val contexto = LocalContext.current


    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var errorLocal by remember { mutableStateOf("") }
    val estadoUi by vm.estadoUi.collectAsState()


    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Iniciar sesion", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Reporta barreras y mejora la accesibilidad en Cadiz.")

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                vm.limpiarError()
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = contrasena,
            onValueChange = {
                contrasena = it
                vm.limpiarError()
            },
            label = { Text("Contrasena") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )


        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                vm.login(
                    email = email,
                    contrasena = contrasena,
                    alEntrar = alLoguear
                )
            },
            enabled = !estadoUi.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (estadoUi.cargando) "Entrando..." else "Entrar")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = {
            vm.limpiarError()
            irARegistro()
        }) {
            Text("Crear cuenta")
        }    }
}
