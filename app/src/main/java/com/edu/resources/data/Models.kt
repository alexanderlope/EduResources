package com.edu.resources.data

/** Recurso educativo tal como lo devuelve MockAPI. Todos los campos son opcionales
 *  porque Gson no aplica valores por defecto de Kotlin al deserializar. */
data class Recurso(
    val id: String = "",
    val titulo: String? = null,
    val descripcion: String? = null,
    val tipo: String? = null,
    val enlace: String? = null,
    val imagen: String? = null,
    val ratings: List<Int>? = null,
    val promedio: Double? = null,
)

data class RecursoInput(
    val titulo: String,
    val descripcion: String,
    val tipo: String,
    val enlace: String,
    val imagen: String,
)

/** Cuerpo del POST: ratings/promedio explícitos para que MockAPI no genere valores aleatorios. */
data class RecursoNuevo(
    val titulo: String,
    val descripcion: String,
    val tipo: String,
    val enlace: String,
    val imagen: String,
    val ratings: List<Int> = emptyList(),
    val promedio: Double = 0.0,
)

data class RatingUpdate(
    val ratings: List<Int>,
    val promedio: Double,
)

const val ROL_ESTUDIANTE = "estudiante"
const val ROL_DOCENTE = "docente"

/** Usuario en sesión (sin credenciales). */
data class User(
    val id: String,
    val nombre: String,
    val email: String,
    val rol: String,
) {
    val isDocente: Boolean get() = rol == ROL_DOCENTE
    val isEstudiante: Boolean get() = rol == ROL_ESTUDIANTE
}

/** Usuario guardado localmente: la contraseña se almacena como hash PBKDF2 + sal. */
data class StoredUser(
    val id: String,
    val nombre: String,
    val email: String,
    val rol: String,
    val salt: String,
    val hash: String,
)

val TIPOS_RECURSO = listOf("libro", "video", "artículo", "tutorial", "curso")
