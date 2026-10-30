package com.edu.resources.util

/** Mismas reglas que utils/validation.js. */
fun validatePassword(password: String): List<String> {
    val errors = mutableListOf<String>()
    if (password.length < 12) errors += "Mínimo 12 caracteres"
    if (!password.any { it in 'A'..'Z' }) errors += "Al menos una letra mayúscula"
    if (!password.any { it in 'a'..'z' }) errors += "Al menos una letra minúscula"
    if (!password.any { it in '0'..'9' }) errors += "Al menos un número"
    if (!password.any { it in "!@#$%^&*" }) errors += "Al menos un carácter especial (!@#$%^&*)"
    return errors
}
