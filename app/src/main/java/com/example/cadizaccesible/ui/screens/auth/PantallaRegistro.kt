package com.example.cadizaccesible.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cadizaccesible.data.users.RolUsuario

@Composable
fun PantallaRegistro(
    volverALogin: () -> Unit,
    vm: AuthViewModel = viewModel()
) {

    LaunchedEffect(Unit) {
        vm.limpiarError()
    }


    val estadoUi by vm.estadoUi.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var codigoAdmin by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf(RolUsuario.CIUDADANO) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Registro", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                vm.limpiarError()
            },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))


        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                vm.limpiarError()
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))


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

        Text("Rol", style = MaterialTheme.typography.titleMedium)

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {

            FilterChip(
                selected = rol == RolUsuario.CIUDADANO,
                onClick = {
                    rol = RolUsuario.CIUDADANO
                    vm.limpiarError()
                },
                label = { Text("Ciudadano") }
            )

            FilterChip(
                selected = rol == RolUsuario.ADMIN,
                onClick = {
                    rol = RolUsuario.ADMIN
                    vm.limpiarError()
                },
                label = { Text("Admin") }
            )
        }

        if (rol == RolUsuario.ADMIN) {
            OutlinedTextField(
                value = codigoAdmin,
                onValueChange = {
                    codigoAdmin = it
                    vm.limpiarError()
                },
                label = { Text("Código admin (si no, serás ciudadano)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (estadoUi.error.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            AssistChip(onClick = {}, label = { Text(estadoUi.error) })
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (nombre.isBlank() || email.isBlank() || contrasena.isBlank()) return@Button

                vm.registrar(
                    nombre = nombre,
                    email = email,
                    contrasena = contrasena,
                    rolElegido = rol,
                    codigoAdmin = codigoAdmin,
                    alRegistrar = { volverALogin() }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !estadoUi.cargando
        ) {
            if (estadoUi.cargando) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Creando...")
            } else {
                Text("Crear cuenta")
            }
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = {
            vm.limpiarError()
            volverALogin()
        }) {
            Text("Volver al login")
        }    }
}
