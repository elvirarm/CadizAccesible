package com.example.cadizaccesible.data.sesion

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Extension property para crear una única instancia de DataStore destinada a la configuración del tema. */
private val Context.dataStore by preferencesDataStore(name = "theme_prefs")

/**
 * Gestor de preferencias de apariencia de la aplicación.
 * * Permite almacenar y consultar la elección del usuario sobre el tema visual (Claro/Oscuro)
 * de forma asíncrona y persistente.
 * * @param context Contexto necesario para acceder al almacenamiento de preferencias.
 */
class ThemePrefs(private val context: Context) {

    companion object {
        /** Clave para almacenar el estado del modo oscuro. */
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    /**
     * Flujo reactivo que emite el estado actual del modo oscuro.
     * * Emite `true` si el modo oscuro está activado y `false` en caso contrario o si no hay valor guardado.
     * Se puede observar en el nivel de UI para cambiar el tema de forma dinámica.
     */
    val darkModeFlow: Flow<Boolean> =
        context.dataStore.data.map { prefs ->
            prefs[DARK_MODE] ?: false
        }

    /**
     * Actualiza y persiste la preferencia del usuario sobre el modo oscuro.
     * @param enabled `true` para activar el modo oscuro, `false` para el modo claro.
     */
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE] = enabled
        }
    }
}