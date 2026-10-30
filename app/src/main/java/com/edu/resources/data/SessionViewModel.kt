package com.edu.resources.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.edu.resources.util.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthException(message: String) : Exception(message)

/** Equivale a AuthContext + FavoritesContext de la app Expo. */
class SessionViewModel(app: Application) : AndroidViewModel(app) {
    private val store = LocalStore(app)

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready.asStateFlow()

    private val _favorites = MutableStateFlow<List<Recurso>>(emptyList())
    val favorites: StateFlow<List<Recurso>> = _favorites.asStateFlow()

    init {
        viewModelScope.launch {
            val saved = withContext(Dispatchers.IO) { store.session() }
            if (saved != null) {
                _favorites.value = withContext(Dispatchers.IO) { store.favorites(saved.id) }
                _user.value = saved
            }
            _ready.value = true
        }
    }

    suspend fun register(nombre: String, email: String, password: String, rol: String) {
        withContext(Dispatchers.Default) {
            val users = store.users()
            if (users.any { it.email == email }) throw AuthException("El correo ya está registrado.")
            val salt = PasswordHasher.newSalt()
            val newUser = StoredUser(
                id = System.currentTimeMillis().toString(),
                nombre = nombre,
                email = email,
                rol = rol,
                salt = salt,
                hash = PasswordHasher.hash(password, salt),
            )
            store.saveUsers(users + newUser)
        }
    }

    suspend fun login(email: String, password: String) {
        val user = withContext(Dispatchers.Default) {
            val found = store.users().find { it.email == email }
            if (found == null || !PasswordHasher.matches(password, found.salt, found.hash)) {
                throw AuthException("Credenciales incorrectas.")
            }
            User(found.id, found.nombre, found.email, found.rol)
        }
        store.saveSession(user)
        _favorites.value = store.favorites(user.id)
        _user.value = user
    }

    fun logout() {
        store.clearSession()
        _favorites.value = emptyList()
        _user.value = null
    }

    fun isFavorite(id: String) = _favorites.value.any { it.id == id }

    fun toggleFavorite(recurso: Recurso) {
        val u = _user.value ?: return
        val current = _favorites.value
        val updated = if (current.any { it.id == recurso.id }) {
            current.filter { it.id != recurso.id }
        } else {
            current + recurso
        }
        _favorites.value = updated
        store.saveFavorites(u.id, updated)
    }

    /** Mantiene al día la copia guardada de un favorito (p. ej. tras calificarlo). */
    fun refreshFavorite(recurso: Recurso) {
        val u = _user.value ?: return
        val current = _favorites.value
        if (current.none { it.id == recurso.id }) return
        val updated = current.map { if (it.id == recurso.id) recurso else it }
        _favorites.value = updated
        store.saveFavorites(u.id, updated)
    }
}
