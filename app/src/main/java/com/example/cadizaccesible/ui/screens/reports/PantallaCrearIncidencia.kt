package com.example.cadizaccesible.ui.screens.reports

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
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
import androidx.compose.ui.Modifier
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

    val categorias = listOf("Aceras", "Rutas", "Semaforos", "Transporte", "Edificios", "Otros")
    val accesibilidades = listOf("Movilidad", "Visual", "Auditiva", "Cognitiva", "General")

    var estadoUbicacion by remember { mutableStateOf("") }

    @SuppressLint("MissingPermission")
    fun obtenerUbicacionActual() {
        val cliente = LocationServices.getFusedLocationProviderClient(contexto)
        estadoUbicacion = "Obteniendo ubicación..."

        cliente.lastLocation
            .addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    vm.onUbicacion(loc.latitude, loc.longitude)
                    estadoUbicacion = "Ubicación guardada: %.5f, %.5f".format(loc.latitude, loc.longitude)
                } else {
                    estadoUbicacion = "No se pudo obtener la ubicación (activa GPS e inténtalo)."
                }
            }
            .addOnFailureListener {
                estadoUbicacion = "Error al obtener ubicación."
            }
    }

    val launcherPermisosUbicacion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val concedido = (permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
                (permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true)

        if (concedido) {
            obtenerUbicacionActual()
        } else {
            estadoUbicacion = "Permiso de ubicación denegado."
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Crear incidencia") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = state.titulo,
                onValueChange = vm::onTitulo,
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = state.descripcion,
                onValueChange = vm::onDescripcion,
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Text("Categoría", style = MaterialTheme.typography.titleMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                categorias.forEach { cat ->
                    FilterChip(
                        selected = state.categoria == cat,
                        onClick = { vm.onCategoria(cat) },
                        label = { Text(cat) }
                    )
                }
            }

            Text("Accesibilidad afectada", style = MaterialTheme.typography.titleMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                accesibilidades.forEach { acc ->
                    FilterChip(
                        selected = state.accesibilidadAfectada == acc,
                        onClick = { vm.onAccesibilidad(acc) },
                        label = { Text(acc) }
                    )
                }
            }

            Text("Gravedad", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Gravedad.entries.forEach { g ->
                    FilterChip(
                        selected = state.gravedad == g,
                        onClick = { vm.onGravedad(g) },
                        label = { Text(g.name) }
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Switch(checked = state.esUrgente, onCheckedChange = vm::onUrgente)
                    Spacer(Modifier.width(8.dp))
                    Text("Urgente")
                }

                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Switch(checked = state.esObstaculoTemporal, onCheckedChange = vm::onObstaculo)
                    Spacer(Modifier.width(8.dp))
                    Text("Obstáculo temporal")
                }
            }

            OutlinedTextField(
                value = state.direccionTexto,
                onValueChange = vm::onDireccion,
                label = { Text("Ubicación (calle, número o referencia)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (state.error.isNotBlank()) {
                AssistChip(onClick = {}, label = { Text(state.error) })
            }

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
                Text(estadoUbicacion, style = MaterialTheme.typography.bodySmall)
            }

            Text("Foto (opcional)", style = MaterialTheme.typography.titleMedium)

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
                        .height(180.dp)
                )
            } else if (state.fotoUri != null) {
                AsyncImage(
                    model = state.fotoUri,
                    contentDescription = "Foto seleccionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = alCancelar,
                    modifier = Modifier.weight(1f),
                    enabled = !state.publicando
                ) { Text("Cancelar") }

                Button(
                    onClick = { vm.publicar(emailUsuario) },
                    modifier = Modifier.weight(1f),
                    enabled = !state.publicando
                ) {
                    if (state.publicando) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Publicando...")
                    } else {
                        Text("Publicar")
                    }
                }
            }
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
