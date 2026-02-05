package com.example.cadizaccesible.ui.screens.reports

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cadizaccesible.data.reports.Gravedad
import com.example.cadizaccesible.data.reports.RepositorioIncidenciasRoom
import com.example.cadizaccesible.ui.components.CampoTextoConVoz
import com.google.android.gms.location.LocationServices
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

/**
 * Pantalla principal para el reporte de nuevas incidencias de accesibilidad.
 * * Centraliza la lógica de:
 * - Captura de imágenes (Cámara y Galería).
 * - Geolocalización (GPS y Geocoding inverso).
 * - Entrada de datos asistida (Dictado por voz).
 * * @param emailUsuario Email del usuario actual para vincular la autoría.
 * @param alCrear Acción a ejecutar tras el guardado exitoso.
 * @param alCancelar Acción para volver atrás.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PantallaCrearIncidencia(
    emailUsuario: String,
    alCrear: () -> Unit,
    alCancelar: () -> Unit
) {
    val contexto = LocalContext.current
    val repo = remember { RepositorioIncidenciasRoom(contexto) }

    // Inicialización del ViewModel mediante Factoría para inyectar el repositorio
    val vm: CrearIncidenciaViewModel = viewModel(
        factory = CrearIncidenciaViewModelFactory(repo)
    )
    val state by vm.ui.collectAsState()

    // Observador para navegar cuando la incidencia se haya guardado con éxito
    LaunchedEffect(state.creadaOk) {
        if (state.creadaOk) {
            vm.consumirCreadaOk()
            alCrear()
        }
    }

    // Estados locales para previsualización inmediata
    var fotoPreviewBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var estadoUbicacion by remember { mutableStateOf("") }

    val categorias = listOf("Aceras", "Rutas", "Semáforos", "Transporte", "Edificios", "Otros")
    val accesibilidades = listOf("Movilidad", "Visual", "Auditiva", "Cognitiva", "General")

    // --- Launchers para Multimedia ---

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            vm.onFoto(it.toString())
            fotoPreviewBitmap = null
        }
    }

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            fotoPreviewBitmap = it
            val uriGuardada = guardarBitmapEnCache(contexto, it)
            vm.onFoto(uriGuardada.toString())
        }
    }

    // --- Lógica de Localización ---

    @SuppressLint("MissingPermission")
    fun obtenerUbicacionActual() {
        val cliente = LocationServices.getFusedLocationProviderClient(contexto)
        estadoUbicacion = "Obteniendo ubicación..."

        cliente.lastLocation.addOnSuccessListener { loc: Location? ->
            if (loc != null) {
                vm.onUbicacion(loc.latitude, loc.longitude)
                val direccion = obtenerDireccionDesdeCoordenadas(contexto, loc.latitude, loc.longitude)
                vm.onDireccion(direccion)
                estadoUbicacion = "Ubicación detectada correctamente"
            } else {
                estadoUbicacion = "No se pudo obtener la señal GPS"
            }
        }.addOnFailureListener {
            estadoUbicacion = "Error en el sensor de ubicación"
        }
    }

    val launcherPermisosUbicacion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val concedido = permisos.values.any { it }
        if (concedido) obtenerUbicacionActual()
        else estadoUbicacion = "Permiso de ubicación denegado."
    }

    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
    )

    // --- Estructura Visual ---

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nueva incidencia") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(18.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Encabezado Informativo
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Describe el problema", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Usa el micrófono para dictar el título o la descripción.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Sección de Textos (Dictado por voz integrado)
            ElevatedCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Descripción", style = MaterialTheme.typography.titleMedium)
                    CampoTextoConVoz(
                        value = state.titulo,
                        onValueChange = vm::onTitulo,
                        label = "Título",
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    CampoTextoConVoz(
                        value = state.descripcion,
                        onValueChange = vm::onDescripcion,
                        label = "Descripción",
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            }

            // Clasificación Mediante Chips
            ElevatedCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Clasificación", style = MaterialTheme.typography.titleMedium)

                    Text("Categoría", style = MaterialTheme.typography.bodyMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        categorias.forEach { cat ->
                            FilterChip(
                                selected = state.categoria == cat,
                                onClick = { vm.onCategoria(cat) },
                                label = { Text(cat) },
                                colors = chipColors
                            )
                        }
                    }

                    Text("Accesibilidad afectada", style = MaterialTheme.typography.bodyMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        accesibilidades.forEach { acc ->
                            FilterChip(
                                selected = state.accesibilidadAfectada == acc,
                                onClick = { vm.onAccesibilidad(acc) },
                                label = { Text(acc) },
                                colors = chipColors
                            )
                        }
                    }

                    Text("Gravedad", style = MaterialTheme.typography.bodyMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Gravedad.entries.forEach { g ->
                            FilterChip(
                                selected = state.gravedad == g,
                                onClick = { vm.onGravedad(g) },
                                label = { Text(g.name) },
                                colors = chipColors
                            )
                        }
                    }
                }
            }

            // Detalles de Ubicación y Switches
            ElevatedCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Ubicación", style = MaterialTheme.typography.titleMedium)

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(checked = state.esUrgente, onCheckedChange = vm::onUrgente)
                            Spacer(Modifier.width(8.dp))
                            Text("Urgente")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(checked = state.esObstaculoTemporal, onCheckedChange = vm::onObstaculo)
                            Spacer(Modifier.width(8.dp))
                            Text("Temporal")
                        }
                    }

                    CampoTextoConVoz(
                        value = state.direccionTexto,
                        onValueChange = vm::onDireccion,
                        label = "Dirección",
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedButton(
                        onClick = {
                            launcherPermisosUbicacion.launch(
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (estadoUbicacion.isEmpty()) "Obtener ubicación GPS" else estadoUbicacion)
                    }
                }
            }

            // Gestión de Imágenes
            ElevatedCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Imagen (opcional)", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { launcherGaleria.launch("image/*") }) { Text("Galería") }
                        OutlinedButton(onClick = { launcherCamara.launch(null) }) { Text("Cámara") }
                        if (state.fotoUri != null) {
                            TextButton(onClick = { vm.onFoto(null); fotoPreviewBitmap = null }) { Text("Quitar") }
                        }
                    }

                    // Renderizado de la imagen seleccionada
                    if (fotoPreviewBitmap != null) {
                        Image(
                            bitmap = fotoPreviewBitmap!!.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().height(200.dp).clip(MaterialTheme.shapes.large),
                            contentScale = ContentScale.Crop
                        )
                    } else if (state.fotoUri != null) {
                        AsyncImage(
                            model = state.fotoUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().height(200.dp).clip(MaterialTheme.shapes.large),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // Botonera Final
            if (state.error.isNotBlank()) {
                Text(state.error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = alCancelar, modifier = Modifier.weight(1f), enabled = !state.publicando) {
                    Text("Cancelar")
                }
                Button(onClick = { vm.publicar(emailUsuario) }, modifier = Modifier.weight(1f), enabled = !state.publicando) {
                    if (state.publicando) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    else Text("Publicar")
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

// --- FUNCIONES DE SOPORTE TÉCNICO ---

/** Guarda un bitmap en el cache temporal para generar una URI persistible. */
private fun guardarBitmapEnCache(contexto: Context, bitmap: Bitmap): Uri {
    val archivo = File(contexto.cacheDir, "incidencia_${System.currentTimeMillis()}.jpg")
    FileOutputStream(archivo).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    return Uri.fromFile(archivo)
}

/** Traduce coordenadas en una dirección legible (Calle, Número, Ciudad). */
fun obtenerDireccionDesdeCoordenadas(context: Context, lat: Double, lng: Double): String {
    return try {
        val geocoder = Geocoder(context, Locale("es", "ES"))
        val direcciones = geocoder.getFromLocation(lat, lng, 1)
        if (!direcciones.isNullOrEmpty()) {
            val d = direcciones[0]
            listOfNotNull(d.thoroughfare, d.subThoroughfare, d.locality).joinToString(", ")
        } else "%.5f, %.5f".format(lat, lng)
    } catch (e: Exception) { "%.5f, %.5f".format(lat, lng) }
}

/** Factoría para instanciar el ViewModel con su repositorio. */
class CrearIncidenciaViewModelFactory(
    private val repo: RepositorioIncidenciasRoom
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CrearIncidenciaViewModel(repo) as T
    }
}