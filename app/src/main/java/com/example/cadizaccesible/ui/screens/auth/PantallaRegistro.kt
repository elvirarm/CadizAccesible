package com.example.cadizaccesible.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cadizaccesible.data.users.RolUsuario
import com.example.cadizaccesible.ui.components.AppCard
import com.example.cadizaccesible.ui.components.HeroCard
import com.example.cadizaccesible.ui.components.Screen
import com.example.cadizaccesible.ui.theme.Dimens
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing

/**
 * Pantalla de registro de nuevos usuarios.
 * * Permite a los ciudadanos crear una cuenta para reportar incidencias.
 * Incluye una lógica condicional para el registro de administradores mediante
 * un código de validación secreto.
 * * @param volverALogin Callback para regresar a la pantalla de inicio de sesión.
 * @param vm ViewModel de autenticación que gestiona la lógica de negocio y persistencia.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistro(
    volverALogin: () -> Unit,
    vm: AuthViewModel = viewModel()
) {
    // Al entrar, aseguramos que no se arrastren errores de intentos previos
    LaunchedEffect(Unit) { vm.limpiarError() }

    val estadoUi by vm.estadoUi.collectAsState()

    // Estados locales para el formulario
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var codigoAdmin by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf(RolUsuario.CIUDADANO) }

    // ... (Configuración de UI y Scaffold)
    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
    )

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Registro") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->

        Screen(
            modifier = Modifier.padding(padding),
            scroll = true
        ) {

            HeroCard(
                title = "Crea tu cuenta",
                subtitle = "Regístrate para reportar incidencias y mejorar la accesibilidad."
            )

            AppCard(title = "Datos de la cuenta") {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.CardGap)) {

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it; vm.limpiarError() },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; vm.limpiarError() },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = { contrasena = it; vm.limpiarError() },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Text("Rol", style = MaterialTheme.typography.titleMedium)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = rol == RolUsuario.CIUDADANO,
                            onClick = { rol = RolUsuario.CIUDADANO; vm.limpiarError() },
                            label = { Text("Ciudadano") },
                            colors = chipColors
                        )

                        FilterChip(
                            selected = rol == RolUsuario.ADMIN,
                            onClick = { rol = RolUsuario.ADMIN; vm.limpiarError() },
                            label = { Text("Admin") },
                            colors = chipColors
                        )
                    }

                    if (rol == RolUsuario.ADMIN) {
                        ElevatedCard(
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(Dimens.CardPadding),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Acceso de administrador",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "Introduce el código para registrarte como admin. Si no es válido, serás ciudadano.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                OutlinedTextField(
                                    value = codigoAdmin,
                                    onValueChange = { codigoAdmin = it; vm.limpiarError() },
                                    label = { Text("Código admin") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    if (estadoUi.error.isNotBlank()) {
                        ElevatedCard(
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = estadoUi.error,
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (nombre.isBlank() || email.isBlank() || contrasena.isBlank()) return@Button

                            vm.registrar(
                                nombre = nombre.trim(),
                                email = email.trim(),
                                contrasena = contrasena,
                                rolElegido = rol,
                                codigoAdmin = codigoAdmin.trim(),
                                alRegistrar = { volverALogin() }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = !estadoUi.cargando
                    ) {
                        if (estadoUi.cargando) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("Creando…")
                        } else {
                            Text("Crear cuenta")
                        }
                    }
                }
            }

            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.CardPadding),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("¿Ya tienes cuenta?", style = MaterialTheme.typography.titleMedium)
                    OutlinedButton(
                        onClick = { vm.limpiarError(); volverALogin() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Volver al login")
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}
