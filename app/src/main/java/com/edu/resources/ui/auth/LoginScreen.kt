package com.edu.resources.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edu.resources.data.SessionViewModel
import com.edu.resources.ui.components.AppTextField
import com.edu.resources.ui.components.MessageDialog
import com.edu.resources.ui.components.PrimaryButton
import com.edu.resources.ui.theme.AppColors
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(session: SessionViewModel, onRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var dialog by remember { mutableStateOf<Pair<String, String>?>(null) }
    val scope = rememberCoroutineScope()

    fun submit() {
        if (email.isBlank() || password.isBlank()) {
            dialog = "Error" to "Por favor completa todos los campos."
            return
        }
        loading = true
        scope.launch {
            try {
                session.login(email.trim().lowercase(), password)
            } catch (e: Exception) {
                dialog = "Error de acceso" to (e.message ?: "No se pudo iniciar sesión.")
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(AppColors.Surface)
                    .border(2.dp, AppColors.Accent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.School, null, tint = AppColors.Accent, modifier = Modifier.size(48.dp))
            }
            Spacer(Modifier.height(14.dp))
            Text("EduResources", color = AppColors.Text, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
            Text("INGENIERÍA DE SISTEMAS", color = AppColors.Accent, fontSize = 13.sp, letterSpacing = 2.sp)
            Spacer(Modifier.height(36.dp))

            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(AppColors.Surface)
                    .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(24.dp))
                    .padding(28.dp),
            ) {
                Text("Bienvenido", color = AppColors.Text, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text("Inicia sesión para continuar", color = AppColors.Muted, fontSize = 14.sp)
                Spacer(Modifier.height(28.dp))

                Text("Correo electrónico", color = AppColors.Muted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                AppTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "tucorreo@ejemplo.com",
                    leadingIcon = Icons.Outlined.Email,
                    keyboardType = KeyboardType.Email,
                )
                Spacer(Modifier.height(18.dp))

                Text("Contraseña", color = AppColors.Muted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                AppTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "••••••••••••",
                    leadingIcon = Icons.Outlined.Lock,
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    showPassword = showPass,
                    trailing = {
                        IconButton(onClick = { showPass = !showPass }) {
                            Icon(
                                if (showPass) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                null,
                                tint = AppColors.Dim,
                            )
                        }
                    },
                )
                Spacer(Modifier.height(24.dp))

                PrimaryButton("Iniciar Sesión", onClick = ::submit, loading = loading)

                Text(
                    buildAnnotatedString {
                        append("¿No tienes cuenta? ")
                        withStyle(SpanStyle(color = AppColors.Accent, fontWeight = FontWeight.Bold)) { append("Regístrate") }
                    },
                    color = AppColors.Dim,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp)
                        .clickable(onClick = onRegister),
                )
            }
        }
    }

    dialog?.let { (title, msg) -> MessageDialog(title, msg, onDismiss = { dialog = null }) }
}
