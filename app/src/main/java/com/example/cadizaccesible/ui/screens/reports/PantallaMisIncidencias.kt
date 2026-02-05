package com.example.cadizaccesible.ui.screens.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cadizaccesible.data.reports.RepositorioIncidenciasRoom
import com.example.cadizaccesible.ui.components.TarjetaIncidencia
import kotlinx.coroutines.launch

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete

/**
 * Pantalla que lista las incidencias reportadas específicamente por el usuario actual.
 * * Esta vista permite al ciudadano realizar un seguimiento de sus propios reportes y
 * gestionar su limpieza mediante gestos de deslizamiento.
 * * Funcionalidades clave:
 * 1. **Filtrado por Propietario**: Solo muestra datos vinculados al [emailUsuario].
 * 2. **Eliminación Intuitiva**: Implementa el patrón "Swipe-to-Dismiss" para borrar reportes.
 * 3. **Navegación al Detalle**: Permite profundizar en cada incidencia pulsando sobre su tarjeta.
 * * @param emailUsuario Identificador del usuario para filtrar la consulta en la base de datos.
 * @param alAbrirDetalle Callback que recibe el ID de la incidencia para navegar a su vista detallada.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PantallaMisIncidencias(
    emailUsuario: String,
    alAbrirDetalle: (String) -> Unit
) {
    val contexto = LocalContext.current
    // Instanciación del repositorio para acceso a datos locales
    val repo = remember { RepositorioIncidenciasRoom(contexto) }
    val scope = rememberCoroutineScope()

    /** * Recolección de la lista de incidencias como un estado de Compose.
     * Se actualiza automáticamente cada vez que la base de datos sufre un cambio.
     */
    val lista by repo
        .obtenerPorCreador(emailUsuario)
        .collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis incidencias") }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            /** Tarjeta de cabecera con instrucciones de uso */
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Tus reportes",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Desliza a la izquierda para eliminar una incidencia.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            /** * Estado de vista vacía: Se muestra cuando el usuario no tiene
             * registros en la base de datos.
             */
            if (lista.isEmpty()) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Aún no has creado incidencias",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Cuando crees una, aparecerá aquí para que puedas consultarla.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                /** * Listado optimizado mediante LazyColumn.
                 * Solo renderiza los elementos visibles en pantalla.
                 */
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = lista,
                        key = { it.id } // Clave única para optimizar recomposiciones y animaciones
                    ) { incidencia ->

                        /** * Gestión del estado del gesto de deslizamiento.
                         * Se activa el borrado físico en el repositorio al completar el gesto.
                         */
                        val estadoSwipe = rememberDismissState(
                            confirmStateChange = { valor ->
                                if (valor == DismissValue.DismissedToStart) {
                                    scope.launch { repo.eliminarIncidencia(incidencia.id) }
                                    true // Confirmar la eliminación visual
                                } else false
                            }
                        )

                        SwipeToDismiss(
                            state = estadoSwipe,
                            directions = setOf(DismissDirection.EndToStart), // Solo permitir deslizar hacia la izquierda
                            background = {
                                FondoSwipeEliminar(direccion = estadoSwipe.dismissDirection)
                            },
                            dismissContent = {
                                TarjetaIncidencia(
                                    incidencia = incidencia,
                                    onClick = { id -> alAbrirDetalle(id) }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Representa la capa visual que aparece "detrás" de la tarjeta cuando se desliza.
 * Indica visualmente la acción de borrado mediante colores y un icono descriptivo.
 */
@Composable
private fun FondoSwipeEliminar(direccion: DismissDirection?) {
    val mostrando = direccion == DismissDirection.EndToStart

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (mostrando) MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                else Color.Transparent
            )
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        if (mostrando) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("ELIMINAR", style = MaterialTheme.typography.titleMedium)
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
            }
        }
    }
}