package com.edu.resources.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edu.resources.data.ROL_DOCENTE
import com.edu.resources.data.ROL_ESTUDIANTE
import com.edu.resources.data.SessionViewModel
import com.edu.resources.ui.components.AppTextField
import com.edu.resources.ui.components.MessageDialog
import com.edu.resources.ui.components.PrimaryButton
import com.edu.resources.ui.components.capitalized
import com.edu.resources.ui.theme.AppColors
import com.edu.resources.util.validatePassword
import kotlinx.coroutines.launch

@Composable
private fun FieldLabel(text: String) {
    Text(text.uppercase(), color = AppColors.Muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
    Spacer(Modifier.height(8.dp))
}

@Composable
fun RegisterScreen(session: SessionViewModel, onBack: () -> Unit, onDone: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf(ROL_ESTUDIANTE) }
    var showPass by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var dialog by remember { mutableStateOf<Pair<String, String>?>(null) }
    var success by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val passErrors = validatePassword(password)

    fun submit() {
        if (nombre.isBlank() || email.isBlank() || password.isEmpty() || confirm.isEmpty()) {
            dialog = "Error" to "Completa todos los campos."
            return
        }
        if (passErrors.isNotEmpty()) {
            dialog = "Contraseña inválida" to passErrors.joinToString("\n")
            return
        }
        if (password != confirm) {
            dialog = "Error" to "Las contraseñas no coinciden."
            return
        }
        loading = true
        scope.launch {
            try {
                session.register(nombre.trim(), email.trim().lowercase(), password, rol)
                success = true
            } catch (e: Exception) {
                dialog = "Error" to (e.message ?: "No se pudo crear la cuenta.")
            } finally {
                loading = false
            }
        }
    }

    Box(Modifier.fillMaxSize().background(AppColors.Bg).imePadding()) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AppColors.Accent)
            }
            Spacer(Modifier.height(12.dp))
            Text("Crear Cuenta", color = AppColors.Text, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(6.dp))
            Text("Únete a EduResources", color = AppColors.Accent, fontSize = 14.sp, letterSpacing = 1.sp)
            Spacer(Modifier.height(28.dp))

            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(AppColors.Surface)
                    .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(24.dp))
                    .padding(24.dp),
            ) {
                FieldLabel("Nombre completo")
                AppTextField(
                    nombre, { nombre = it },
                    placeholder = "Tu nombre",
                    leadingIcon = Icons.Outlined.Person,
                    capitalization = KeyboardCapitalization.Words,
                )
                Spacer(Modifier.height(18.dp))

                FieldLabel("Correo electrónico")
                AppTextField(
                    email, { email = it },
                    placeholder = "tucorreo@ejemplo.com",
                    leadingIcon = Icons.Outlined.Email,
                    keyboardType = KeyboardType.Email,
                )
                Spacer(Modifier.height(18.dp))

                FieldLabel("Rol")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf(ROL_ESTUDIANTE, ROL_DOCENTE).forEach { r ->
                        val active = rol == r
                        val shape = RoundedCornerShape(12.dp)
                        Row(
                            Modifier
                                .weight(1f)
                                .clip(shape)
                                .background(if (active) AppColors.Accent else androidx.compose.ui.graphics.Color.Transparent)
                                .border(1.5.dp, AppColors.Accent, shape)
                                .clickable { rol = r }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                if (r == ROL_ESTUDIANTE) Icons.Outlined.School else Icons.Outlined.Work,
                                null,
                                tint = if (active) AppColors.Bg else AppColors.Accent,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.size(8.dp))
                            Text(
                                r.capitalized(),
                                color = if (active) AppColors.Bg else AppColors.Accent,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))

                FieldLabel("Contraseña")
                AppTextField(
                    password, { password = it },
                    placeholder = "Mínimo 12 caracteres",
                    leadingIcon = Icons.Outlined.Lock,
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    showPassword = showPass,
                    trailing = {
                        IconButton(onClick = { showPass = !showPass }) {
                            Icon(
                                if (showPass) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                null, tint = AppColors.Dim,
                            )
                        }
                    },
                )
                if (password.isNotEmpty() && passErrors.isNotEmpty()) {
                    Column(
                        Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(androidx.compose.ui.graphics.Color(0xFF1C0F0F))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        passErrors.forEach { err ->
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Filled.Cancel, null, tint = AppColors.Red, modifier = Modifier.size(14.dp))
                                Text(err, color = AppColors.Red, fontSize = 12.sp)
                            }
                        }
                    }
                }
                if (password.isNotEmpty() && passErrors.isEmpty()) {
                    Row(
                        Modifier.padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(Icons.Filled.CheckCircle, null, tint = AppColors.Accent, modifier = Modifier.size(14.dp))
                        Text("Contraseña válida", color = AppColors.Accent, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(18.dp))

                FieldLabel("Confirmar contraseña")
                AppTextField(
                    confirm, { confirm = it },
                    placeholder = "Repite tu contraseña",
                    leadingIcon = Icons.Outlined.Lock,
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    showPassword = showPass,
                )
                Spacer(Modifier.height(24.dp))

                PrimaryButton("Crear Cuenta", onClick = ::submit, loading = loading)

                Text(
                    buildAnnotatedString {
                        append("¿Ya tienes cuenta? ")
                        withStyle(SpanStyle(color = AppColors.Accent, fontWeight = FontWeight.Bold)) { append("Inicia sesión") }
                    },
                    color = AppColors.Dim,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 18.dp)
                        .clickable(onClick = onBack),
                )
            }
        }
    }

    dialog?.let { (title, msg) -> MessageDialog(title, msg, onDismiss = { dialog = null }) }
    if (success) {
        MessageDialog(
            title = "¡Éxito!",
            message = "Cuenta creada. Ahora puedes iniciar sesión.",
            onDismiss = { success = false; onDone() },
        )
    }
}
