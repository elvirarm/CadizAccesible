package com.example.cadizaccesible.ui.components

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Botón interactivo que activa el motor de reconocimiento de voz del sistema Android.
 * * Este componente gestiona la comunicación con el servicio de Speech-to-Text de Google.
 * Al ser pulsado, lanza una actividad del sistema que escucha al usuario y devuelve
 * el texto transcrito al callback [onTextRecognized].
 * * @param onTextRecognized Función que recibe el String transcrito tras el dictado exitoso.
 * @param locale Configuración regional para el reconocimiento de voz (por defecto, español de España).
 */
@Composable
fun VoiceInputButton(
    onTextRecognized: (String) -> Unit,
    locale: Locale = Locale("es", "ES")
) {
    val context = LocalContext.current

    /**
     * Lanzador de actividad para gestionar el resultado del dictado.
     * Recupera la lista de posibles resultados y selecciona el de mayor precisión (el primero).
     */
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val texto = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
                .orEmpty()

            if (texto.isNotBlank()) {
                onTextRecognized(texto)
            }
        }
    }

    IconButton(
        onClick = {
            // Configuración del Intent para el reconocimiento de voz
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale.toLanguageTag())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora…")
            }
            // Lanza el asistente de voz del sistema
            launcher.launch(intent)
        }
    ) {
        Icon(
            imageVector = Icons.Filled.Mic,
            contentDescription = "Dictar por voz"
        )
    }
}