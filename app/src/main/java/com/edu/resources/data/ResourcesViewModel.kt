package com.edu.resources.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Lista compartida de recursos (la usan Explorar, Detalle y Gestión). */
class ResourcesViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<Recurso>>(emptyList())
    val items: StateFlow<List<Recurso>> = _items.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _refreshing = MutableStateFlow(false)
    val refreshing: StateFlow<Boolean> = _refreshing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _loading.value = _items.value.isEmpty()
            fetch()
            _loading.value = false
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _refreshing.value = true
            fetch()
            _refreshing.value = false
        }
    }

    private suspend fun fetch() {
        try {
            _items.value = Api.service.getAll()
            _error.value = null
        } catch (e: Exception) {
            Log.e("EduResources", "Fallo al cargar recursos", e)
            val detalle = when (e) {
                is retrofit2.HttpException -> "El servidor respondió HTTP ${e.code()}. Revisa la URL en Api.kt."
                is com.google.gson.JsonParseException -> "El formato de los datos no es el esperado (revisa que ratings sea Array en MockAPI)."
                is java.io.IOException -> "Sin conexión o no se pudo llegar al servidor."
                else -> "${e.javaClass.simpleName}: ${e.message}"
            }
            _error.value = "No se pudieron cargar los recursos.\n\n$detalle"
        }
    }

    fun clearError() { _error.value = null }

    suspend fun create(input: RecursoInput) {
        val created = Api.service.create(
            RecursoNuevo(input.titulo, input.descripcion, input.tipo, input.enlace, input.imagen)
        )
        _items.value = _items.value + created
    }

    suspend fun update(id: String, input: RecursoInput) {
        val updated = Api.service.update(id, input)
        _items.value = _items.value.map { if (it.id == id) updated else it }
    }

    suspend fun delete(id: String) {
        Api.service.delete(id)
        _items.value = _items.value.filter { it.id != id }
    }

    suspend fun rate(recurso: Recurso, stars: Int): Recurso {
        val updated = Api.service.rate(recurso.id, Api.buildRating(recurso.ratings, stars))
        _items.value = _items.value.map { if (it.id == recurso.id) updated else it }
        return updated
    }
}
