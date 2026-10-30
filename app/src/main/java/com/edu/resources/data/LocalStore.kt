package com.edu.resources.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/** Persistencia local (equivalente al AsyncStorage de la app Expo). */
class LocalStore(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("edu_resources", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun users(): List<StoredUser> {
        val json = prefs.getString(KEY_USERS, null) ?: return emptyList()
        return runCatching {
            gson.fromJson<List<StoredUser>>(json, object : TypeToken<List<StoredUser>>() {}.type)
        }.getOrNull() ?: emptyList()
    }

    fun saveUsers(users: List<StoredUser>) {
        prefs.edit().putString(KEY_USERS, gson.toJson(users)).apply()
    }

    fun session(): User? {
        val json = prefs.getString(KEY_SESSION, null) ?: return null
        return runCatching { gson.fromJson(json, User::class.java) }.getOrNull()
    }

    fun saveSession(user: User) {
        prefs.edit().putString(KEY_SESSION, gson.toJson(user)).apply()
    }

    fun clearSession() {
        prefs.edit().remove(KEY_SESSION).apply()
    }

    fun favorites(userId: String): List<Recurso> {
        val json = prefs.getString(favKey(userId), null) ?: return emptyList()
        return runCatching {
            gson.fromJson<List<Recurso>>(json, object : TypeToken<List<Recurso>>() {}.type)
        }.getOrNull() ?: emptyList()
    }

    fun saveFavorites(userId: String, list: List<Recurso>) {
        prefs.edit().putString(favKey(userId), gson.toJson(list)).apply()
    }

    private fun favKey(userId: String) = "favorites_$userId"

    private companion object {
        const val KEY_USERS = "users"
        const val KEY_SESSION = "session"
    }
}
