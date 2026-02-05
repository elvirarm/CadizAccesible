package com.example.cadizaccesible.data.sesion

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.cadizaccesible.data.users.RolUsuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Extension property para instanciar el DataStore de forma única en la aplicación. */
private val Context.dataStore by preferencesDataStore(name = "sesion_store")

/**
 * Gestor encargado de la persistencia de los datos de sesión del usuario.
 * * Utiliza Jetpack DataStore para almacenar de forma segura y reactiva si el usuario
 * está autenticado, su correo electrónico y su rol dentro de la plataforma.
 * * @param contexto Contexto de la aplicación necesario para acceder al DataStore.
 */
class GestorSesion(private val contexto: Context) {

    // Claves de acceso para los valores almacenados en DataStore
    private val CLAVE_LOGUEADO = booleanPreferencesKey("logueado")
    private val CLAVE_EMAIL = stringPreferencesKey("email")
    private val CLAVE_ROL = stringPreferencesKey("rol")

    /**
     * Flujo reactivo que emite el estado de la sesión actual.
     * * Cada vez que se modifican los datos en el DataStore, este [Flow] emite un nuevo
     * objeto [Sesion]. Incluye un manejo de errores mediante [runCatching] por si el
     * valor del rol almacenado no coincide con el Enum actual.
     */
    val flujoSesion: Flow<Sesion> = contexto.dataStore.data.map { prefs ->
        val logueado = prefs[CLAVE_LOGUEADO] ?: false
        val email = prefs[CLAVE_EMAIL] ?: ""
        val rolStr = prefs[CLAVE_ROL] ?: RolUsuario.CIUDADANO.name

        // Intenta convertir el String guardado al Enum RolUsuario, por defecto CIUDADANO
        val rol = runCatching { RolUsuario.valueOf(rolStr) }.getOrDefault(RolUsuario.CIUDADANO)

        Sesion(estaLogueado = logueado, email = email, rol = rol)
    }

    /**
     * Persiste los datos de autenticación del usuario.
     * * @param email Correo electrónico que identifica al usuario.
     * @param rol Nivel de permisos asignado al usuario ([RolUsuario]).
     */
    suspend fun guardarSesion(email: String, rol: RolUsuario) {
        contexto.dataStore.edit { prefs ->
            prefs[CLAVE_LOGUEADO] = true
            prefs[CLAVE_EMAIL] = email
            prefs[CLAVE_ROL] = rol.name
        }
    }

    /**
     * Limpia los datos de la sesión actual, estableciendo el estado a no logueado
     * y reseteando el rol a los valores por defecto.
     */
    suspend fun cerrarSesion() {
        contexto.dataStore.edit { prefs ->
            prefs[CLAVE_LOGUEADO] = false
            prefs[CLAVE_EMAIL] = ""
            prefs[CLAVE_ROL] = RolUsuario.CIUDADANO.name
        }
    }
}