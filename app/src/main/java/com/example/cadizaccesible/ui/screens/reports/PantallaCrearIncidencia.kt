package com.example.cadizaccesible.ui.screens.reports


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import java.io.File
import java.io.FileOutputStream
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cadizaccesible.data.reports.Gravedad
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import com.google.android.gms.location.LocationServices



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearIncidencia(
    emailUsuario: String,
    alCrear: () -> Unit,
    alCancelar: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    val contexto = LocalContext.current

    var fotoUri by remember { mutableStateOf<String?>(null) }
    var fotoPreviewBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            fotoUri = uri.toString()
            fotoPreviewBitmap = null // usamos uri (sin convertir)
        }
    }

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            fotoPreviewBitmap = bitmap
            val uriGuardada = guardarBitmapEnCache(contexto, bitmap)
            fotoUri = uriGuardada.toString()
        }
    }




    // Selectores simples (luego los refinamos)
    val categorias = listOf("Aceras", "Rutas", "Semaforos", "Transporte", "Edificios", "Otros")
    val accesibilidades = listOf("Movilidad", "Visual", "Auditiva", "Cognitiva", "General")

    var categoriaSeleccionada by remember { mutableStateOf(categorias.first()) }
    var accesibilidadSeleccionada by remember { mutableStateOf(accesibilidades.first()) }

    var gravedad by remember { mutableStateOf(Gravedad.MEDIA) }
    var esUrgente by remember { mutableStateOf(false) }
    var esObstaculoTemporal by remember { mutableStateOf(false) }

    var direccionTexto by remember { mutableStateOf("") } // por ahora manual simple
    var latitud by remember { mutableStateOf<Double?>(null) }
    var longitud by remember { mutableStateOf<Double?>(null) }
    var estadoUbicacion by remember { mutableStateOf("") }


    var error by remember { mutableStateOf("") }

    val launcherPermisosUbicacion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val concedido = (permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
                (permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true)

        if (!concedido) {
            estadoUbicacion = "Permiso de ubicacion denegado."
        }
    }

    @SuppressLint("MissingPermission")
    fun obtenerUbicacionActual() {
        val cliente = LocationServices.getFusedLocationProviderClient(contexto)
        estadoUbicacion = "Obteniendo ubicacion..."

        cliente.lastLocation
            .addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    latitud = loc.latitude
                    longitud = loc.longitude
                    estadoUbicacion = "Ubicacion guardada: %.5f, %.5f".format(latitud, longitud)
                } else {
                    estadoUbicacion = "No se pudo obtener la ubicacion (activa GPS e intentalo)."
                }
            }
            .addOnFailureListener {
                estadoUbicacion = "Error al obtener ubicacion."
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear incidencia") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp).
                verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Text("Categoria", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                categorias.forEach { cat ->
                    FilterChip(
                        selected = categoriaSeleccionada == cat,
                        onClick = { categoriaSeleccionada = cat },
                        label = { Text(cat) }
                    )
                }
            }

            Text("Accesibilidad afectada", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                accesibilidades.forEach { acc ->
                    FilterChip(
                        selected = accesibilidadSeleccionada == acc,
                        onClick = { accesibilidadSeleccionada = acc },
                        label = { Text(acc) }
                    )
                }
            }

            Text("Gravedad", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Gravedad.values().forEach { g ->
                    FilterChip(
                        selected = gravedad == g,
                        onClick = { gravedad = g },
                        label = { Text(g.name) }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Switch(checked = esUrgente, onCheckedChange = { esUrgente = it })
                    Spacer(Modifier.width(8.dp))
                    Text("Urgente")
                }

                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Switch(checked = esObstaculoTemporal, onCheckedChange = { esObstaculoTemporal = it })
                    Spacer(Modifier.width(8.dp))
                    Text("Obstáculo temporal")
                }
            }

            OutlinedTextField(
                value = direccionTexto,
                onValueChange = { direccionTexto = it },
                label = { Text("Ubicación (calle, número o referencia)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (error.isNotBlank()) {
                AssistChip(onClick = {}, label = { Text(error) })
            }

            OutlinedButton(
                onClick = {
                    launcherPermisosUbicacion.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                    // Intentamos obtener ubicacion; si no hay permiso, fallara y lo veras en estadoUbicacion
                    // (si quieres perfecto perfecto, lo hacemos condicionado, pero asi es rapido y funcional)
                    obtenerUbicacionActual()
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
                OutlinedButton(
                    onClick = { launcherGaleria.launch("image/*") }
                ) { Text("Galeria") }

                OutlinedButton(
                    onClick = { launcherCamara.launch(null) }
                ) { Text("Cámara") }

                if (fotoUri != null) {
                    TextButton(
                        onClick = { fotoUri = null; fotoPreviewBitmap = null }
                    ) { Text("Quitar") }
                }
            }

            if (fotoPreviewBitmap != null) {
                Spacer(Modifier.height(8.dp))
                Image(
                    bitmap = fotoPreviewBitmap!!.asImageBitmap(),
                    contentDescription = "Foto de la incidencia",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            } else if (fotoUri != null) {
                Spacer(Modifier.height(8.dp))
                AsyncImage(
                    model = fotoUri,
                    contentDescription = "Foto seleccionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = alCancelar,
                    modifier = Modifier.weight(1f)
                ) { Text("Cancelar") }

                Button(
                    onClick = {
                        val t = titulo.trim()
                        val d = descripcion.trim()
                        val dir = direccionTexto.trim()

                        if (t.isBlank()) {
                            error = "El titulo es obligatorio."
                            return@Button
                        }
                        if (d.isBlank()) {
                            error = "La descripcion es obligatoria."
                            return@Button
                        }
                        if (dir.isBlank()) {
                            error = "La ubicacion es obligatoria (aunque sea aproximada)."
                            return@Button
                        }

                        error = ""

                        RepositorioIncidencias.crearIncidencia(
                            emailCreador = emailUsuario,
                            titulo = t,
                            descripcion = d,
                            categoria = categoriaSeleccionada,
                            accesibilidadAfectada = accesibilidadSeleccionada,
                            gravedad = gravedad,
                            esUrgente = esUrgente,
                            esObstaculoTemporal = esObstaculoTemporal,
                            direccionTexto = dir,
                            latitud = latitud,
                            longitud = longitud,
                            fotoUri = fotoUri
                        )

                        alCrear()
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Publicar") }
            }

            OutlinedButton(
                onClick = {
                    launcherPermisosUbicacion.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                    // Intentamos obtener ubicacion; si no hay permiso, fallara y lo veras en estadoUbicacion
                    // (si quieres perfecto perfecto, lo hacemos condicionado, pero asi es rapido y funcional)
                    obtenerUbicacionActual()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Usar ubicacion actual")
            }

            if (estadoUbicacion.isNotBlank()) {
                Text(estadoUbicacion, style = MaterialTheme.typography.bodySmall)
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
