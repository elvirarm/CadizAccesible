package com.example.cadizaccesible.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Campo de texto personalizado con soporte integrado para entrada por voz.
 * * Basado en [OutlinedTextField] de Material3, este componente añade un botón en la
 * parte final (trailing icon) que activa el reconocimiento de voz del dispositivo.
 *
 * @param value Valor actual del texto mostrado en el campo.
 * @param onValueChange Callback que se dispara al cambiar el texto (manualmente o por voz).
 * @param label Etiqueta flotante que describe el propósito del campo.
 * @param modifier [Modifier] para ajustar el diseño y comportamiento del componente.
 * @param singleLine Si es true, el campo se limita a una sola línea horizontal.
 * @param minLines Número mínimo de líneas visibles (útil para descripciones largas).
 * @param habilitarVoz Determina si se muestra el botón de dictado por voz.
 * @param anexarDictado Si es true, el texto dictado se añade al final del texto existente.
 * Si es false, el dictado sustituye todo el contenido actual.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoTextoConVoz(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    minLines: Int = 1,
    habilitarVoz: Boolean = true,
    anexarDictado: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        singleLine = singleLine,
        minLines = minLines,
        trailingIcon = {
            if (habilitarVoz) {
                // Componente que gestiona el Intent de Google Speech-to-Text
                VoiceInputButton(
                    onTextRecognized = { texto ->
                        val nuevo = if (anexarDictado) {
                            // Concatena con un espacio si ya hay texto previo
                            if (value.isEmpty()) texto.trim() else "$value $texto".trim()
                        } else {
                            texto.trim()
                        }
                        onValueChange(nuevo)
                    }
                )
            }
        }
    )
}