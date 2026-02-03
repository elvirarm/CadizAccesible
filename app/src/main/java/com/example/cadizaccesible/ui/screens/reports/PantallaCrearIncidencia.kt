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
import androidx.compose.foundation.layout.FlowRow
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
import coil.compose.AsyncImage
import com.example.cadizaccesible.data.reports.Gravedad
import com.example.cadizaccesible.data.reports.RepositorioIncidenciasRoom
import com.google.android.gms.location.LocationServices
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import com.example.cadizaccesible.ui.components.CampoTextoConVoz

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearIncidencia(
    emailUsuario: String,
    alCrear: () -> Unit,
    alCancelar: () -> Unit
) {
    val contexto = LocalContext.current

    val repo = remember { RepositorioIncidenciasRoom(contexto) }
    val vm = remember { CrearIncidenciaViewModel(repo) }
    val state by vm.ui.collectAsState()

    LaunchedEffect(state.creadaOk) {
        if (state.creadaOk) {
            vm.consumirCreadaOk()
            alCrear()
        }
    }

    var fotoPreviewBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var estadoUbicacion by remember { mutableStateOf("") }

    val categorias = listOf("Aceras", "Rutas", "Semáforos", "Transporte", "Edificios", "Otros")
    val accesibilidades = listOf("Movilidad", "Visual", "Auditiva", "Cognitiva", "General")

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            vm.onFoto(uri.toString())
            fotoPreviewBitmap = null
        }
    }

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            fotoPreviewBitmap = bitmap
            val uriGuardada = guardarBitmapEnCache(contexto, bitmap)
            vm.onFoto(uriGuardada.toString())
        }
    }

    @SuppressLint("MissingPermission")
    fun obtenerUbicacionActual() {
        val cliente = LocationServices.getFusedLocationProviderClient(contexto)
        estadoUbicacion = "Obteniendo ubicación..."

        cliente.lastLocation
            .addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    vm.onUbicacion(loc.latitude, loc.longitude)

                    val direccion = obtenerDireccionDesdeCoordenadas(
                        contexto,
                        loc.latitude,
                        loc.longitude
                    )
                    vm.onDireccion(direccion)

                    estadoUbicacion = "Ubicación detectada"
                } else {
                    estadoUbicacion = "No se pudo obtener la ubicación"
                }
            }
            .addOnFailureListener {
                estadoUbicacion = "Error al obtener ubicación"
            }
    }

    val launcherPermisosUbicacion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val concedido = (permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
                (permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true)

        if (concedido) obtenerUbicacionActual()
        else estadoUbicacion = "Permiso de ubicación denegado."
    }

    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
    )

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

            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Describe el problema", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Añade título, descripción y ubicación. Puedes dictar con el micrófono.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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

            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Clasificación", style = MaterialTheme.typography.titleMedium)

                    Text("Categoría", style = MaterialTheme.typography.bodyMedium)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categorias.forEach { cat ->
                            FilterChip(
                                selected = state.categoria == cat,
                                onClick = { vm.onCategoria(cat) },
                                label = { Text(cat, maxLines = 1, softWrap = false) },
                                colors = chipColors
                            )
                        }
                    }

                    Text("Accesibilidad afectada", style = MaterialTheme.typography.bodyMedium)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        accesibilidades.forEach { acc ->
                            FilterChip(
                                selected = state.accesibilidadAfectada == acc,
                                onClick = { vm.onAccesibilidad(acc) },
                                label = { Text(acc, maxLines = 1, softWrap = false) },
                                colors = chipColors
                            )
                        }
                    }

                    Text("Gravedad", style = MaterialTheme.typography.bodyMedium)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
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

            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Detalles", style = MaterialTheme.typography.titleMedium)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = state.esUrgente,
                                onCheckedChange = vm::onUrgente,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Urgente")
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = state.esObstaculoTemporal,
                                onCheckedChange = vm::onObstaculo,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Obstáculo temporal")
                        }
                    }

                    CampoTextoConVoz(
                        value = state.direccionTexto,
                        onValueChange = vm::onDireccion,
                        label = "Ubicación",
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedButton(
                        onClick = {
                            launcherPermisosUbicacion.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Usar ubicación actual")
                    }

                    if (estadoUbicacion.isNotBlank()) {
                        Text(
                            estadoUbicacion,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (state.error.isNotBlank()) {
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = state.error,
                        modifier = Modifier.padding(14.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Imagen (opcional)", style = MaterialTheme.typography.titleMedium)

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { launcherGaleria.launch("image/*") }) {
                            Text("Galería")
                        }
                        OutlinedButton(onClick = { launcherCamara.launch(null) }) {
                            Text("Cámara")
                        }
                        if (state.fotoUri != null) {
                            TextButton(onClick = { vm.onFoto(null); fotoPreviewBitmap = null }) {
                                Text("Quitar")
                            }
                        }
                    }

                    if (fotoPreviewBitmap != null) {
                        Image(
                            bitmap = fotoPreviewBitmap!!.asImageBitmap(),
                            contentDescription = "Foto de la incidencia",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .clip(MaterialTheme.shapes.large),
                            contentScale = ContentScale.Crop
                        )
                    } else if (state.fotoUri != null) {
                        AsyncImage(
                            model = state.fotoUri,
                            contentDescription = "Foto seleccionada",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .clip(MaterialTheme.shapes.large),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = alCancelar,
                    modifier = Modifier.weight(1f),
                    enabled = !state.publicando
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = { vm.publicar(emailUsuario) },
                    modifier = Modifier.weight(1f),
                    enabled = !state.publicando
                ) {
                    if (state.publicando) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Publicando...")
                    } else {
                        Text("Publicar")
                    }
                }
            }

            Spacer(Modifier.height(6.dp))
        }
    }
}

private fun guardarBitmapEnCache(contexto: Context, bitmap: Bitmap): Uri {
    val archivo = File(contexto.cacheDir, "incidencia_${System.currentTimeMillis()}.jpg")
    FileOutputStream(archivo).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    return Uri.fromFile(archivo)
}

fun obtenerDireccionDesdeCoordenadas(
    context: Context,
    lat: Double,
    lng: Double
): String {
    return try {
        val geocoder = Geocoder(context, Locale("es", "ES"))
        val direcciones = geocoder.getFromLocation(lat, lng, 1)

        if (!direcciones.isNullOrEmpty()) {
            val d = direcciones[0]
            val texto = listOfNotNull(
                d.thoroughfare,        // Calle
                d.subThoroughfare,     // Número
                d.locality             // Ciudad
            ).joinToString(", ")

            if (texto.isNotBlank()) texto else "%.5f, %.5f".format(lat, lng)
        } else {
            "%.5f, %.5f".format(lat, lng)
        }
    } catch (e: Exception) {
        "%.5f, %.5f".format(lat, lng)
    }
}
