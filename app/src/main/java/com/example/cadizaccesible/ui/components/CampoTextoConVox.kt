package com.example.cadizaccesible.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
                VoiceInputButton(
                    onTextRecognized = { texto ->
                        val nuevo = if (anexarDictado) {
                            (value + " " + texto).trim()
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
