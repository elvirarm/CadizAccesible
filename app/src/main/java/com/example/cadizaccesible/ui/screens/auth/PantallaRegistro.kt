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
    val estadoUi by vm.estadoUi.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contraseña by remember { mutableStateOf("") }
    var codigoAdmin by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf(RolUsuario.CIUDADANO) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Registro", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            contraseña, { contraseña = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(16.dp))
        Text("Rol", style = MaterialTheme.typography.titleMedium)

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChip(
                selected = rol == RolUsuario.CIUDADANO,
                onClick = { rol = RolUsuario.CIUDADANO },
                label = { Text("Ciudadano") }
            )
            FilterChip(
                selected = rol == RolUsuario.ADMIN,
                onClick = { rol = RolUsuario.ADMIN },
                label = { Text("Admin") }
            )
        }

        if (rol == RolUsuario.ADMIN) {
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = codigoAdmin,
                onValueChange = { codigoAdmin = it },
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
                vm.registrar(
                    nombre = nombre,
                    email = email,
                    contraseña = contraseña,
                    rolElegido = rol,
                    codigoAdmin = codigoAdmin,
                    alRegistrar = { volverALogin() }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !estadoUi.cargando
        ) {
            if (estadoUi.cargando) CircularProgressIndicator(strokeWidth = 2.dp)
            else Text("Crear cuenta")
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = volverALogin) { Text("Volver al login") }
    }
}
