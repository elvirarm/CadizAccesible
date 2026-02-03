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
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.cadizaccesible.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLogin(
    irARegistro: () -> Unit,
    alLoguear: (RolUsuario) -> Unit,
    vm: AuthViewModel = viewModel()
) {
    LaunchedEffect(Unit) { vm.limpiarError() }

    val estadoUi by vm.estadoUi.collectAsState()
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    Scaffold(contentWindowInsets = WindowInsets.safeDrawing) { padding ->

        Screen(
            modifier = Modifier.padding(padding),
            scroll = true
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_cadiz),
                    contentDescription = "Logo Cádiz Accesible",
                    modifier = Modifier.size(150.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(Modifier.height(8.dp))

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f)
                )
            }
            Spacer(Modifier.height(2.dp))


            HeroCard(
                title = "Cádiz Accesible",
                subtitle = "Reporta barreras y ayuda a mejorar la accesibilidad de tu ciudad."
            )

            AppCard(title = "Acceso") {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.CardGap)) {

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

                    if (estadoUi.error.isNotBlank()) {
                        ElevatedCard(
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
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
                            vm.login(
                                email = email.trim(),
                                contrasena = contrasena,
                                alEntrar = alLoguear
                            )
                        },
                        enabled = !estadoUi.cargando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        if (estadoUi.cargando) {
                            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(10.dp))
                            Text("Entrando…")
                        } else Text("Entrar")
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
                    Text("¿No tienes cuenta?", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Crea una cuenta para reportar incidencias y hacer seguimiento.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedButton(
                        onClick = { vm.limpiarError(); irARegistro() },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Crear cuenta") }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}
