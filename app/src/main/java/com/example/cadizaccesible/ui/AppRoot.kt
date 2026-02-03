package com.example.cadizaccesible.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.cadizaccesible.data.sesion.ThemePrefs
import com.example.cadizaccesible.ui.navigation.HostNavegacion
import com.example.cadizaccesible.ui.theme.CadizAccesibleTheme

@Composable
fun AppRoot() {
    val context = LocalContext.current
    val prefs = remember { ThemePrefs(context) }

    val darkMode by prefs.darkModeFlow.collectAsState(initial = false)

    CadizAccesibleTheme(darkTheme = darkMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HostNavegacion()
        }
    }
}
