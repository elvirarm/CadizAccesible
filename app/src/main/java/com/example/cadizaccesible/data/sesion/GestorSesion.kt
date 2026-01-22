package com.example.cadizaccesible.data.sesion

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.cadizaccesible.data.users.RolUsuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "sesion_store")

class GestorSesion(private val contexto: Context) {

    private val CLAVE_LOGUEADO = booleanPreferencesKey("logueado")
    private val CLAVE_EMAIL = stringPreferencesKey("email")
    private val CLAVE_ROL = stringPreferencesKey("rol")

    val flujoSesion: Flow<Sesion> = contexto.dataStore.data.map { prefs ->
        val logueado = prefs[CLAVE_LOGUEADO] ?: false
        val email = prefs[CLAVE_EMAIL] ?: ""
        val rolStr = prefs[CLAVE_ROL] ?: RolUsuario.CIUDADANO.name
        val rol = runCatching { RolUsuario.valueOf(rolStr) }.getOrDefault(RolUsuario.CIUDADANO)
        Sesion(estaLogueado = logueado, email = email, rol = rol)
    }

    suspend fun guardarSesion(email: String, rol: RolUsuario) {
        contexto.dataStore.edit { prefs ->
            prefs[CLAVE_LOGUEADO] = true
            prefs[CLAVE_EMAIL] = email
            prefs[CLAVE_ROL] = rol.name
        }
    }

    suspend fun cerrarSesion() {
        contexto.dataStore.edit { prefs ->
            prefs[CLAVE_LOGUEADO] = false
            prefs[CLAVE_EMAIL] = ""
            prefs[CLAVE_ROL] = RolUsuario.CIUDADANO.name
        }
    }
}