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
fun PantallaLogin(
    irARegistro: () -> Unit,
    alLoguear: (RolUsuario) -> Unit,
    vm: AuthViewModel = viewModel()
) {
    val estadoUi by vm.estadoUi.collectAsState()

    var email by remember { mutableStateOf("") }
    var contraseña by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Iniciar sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Reporta barreras y mejora la accesibilidad en Cádiz.")

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = contraseña,
            onValueChange = { contraseña = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        if (estadoUi.error.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            AssistChip(onClick = {}, label = { Text(estadoUi.error) })
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { vm.login(email, contraseña, alEntrar = alLoguear) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !estadoUi.cargando
        ) {
            if (estadoUi.cargando) CircularProgressIndicator(strokeWidth = 2.dp)
            else Text("Entrar")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = irARegistro) {
            Text("Crear cuenta")
        }
    }
}
