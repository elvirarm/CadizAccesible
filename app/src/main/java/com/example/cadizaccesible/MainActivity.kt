package com.example.cadizaccesible

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cadizaccesible.ui.navigation.HostNavegacion
import com.example.cadizaccesible.ui.theme.CadizAccesibleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CadizAccesibleTheme {
                HostNavegacion()
            }
        }
    }
}