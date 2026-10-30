package com.edu.resources.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import com.edu.resources.data.User
import com.edu.resources.ui.components.MessageDialog
import com.edu.resources.ui.theme.AppColors

private data class Perm(val icon: ImageVector, val text: String, val ok: Boolean)

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(AppColors.Accent.copy(alpha = 0.07f)),
            contentAlignment = Alignment.Center,
        ) { Icon(icon, null, tint = AppColors.Accent, modifier = Modifier.size(18.dp)) }
        Column(Modifier.weight(1f)) {
            Text(label.uppercase(), color = AppColors.Dimmer, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
            Text(value, color = AppColors.Text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
fun ProfileScreen(user: User, onLogout: () -> Unit) {
    var confirm by remember { mutableStateOf(false) }
    val isDocente = user.isDocente
    val rolColor = if (isDocente) AppColors.Yellow else AppColors.Accent
    val rolLabel = if (isDocente) "Docente" else "Estudiante"
    val cardShape = RoundedCornerShape(16.dp)

    Column(
        Modifier.fillMaxSize().background(AppColors.Bg).verticalScroll(rememberScrollState()).statusBarsPadding().padding(horizontal = 20.dp),
    ) {
        Column(Modifier.fillMaxWidth().padding(top = 36.dp, bottom = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier.size(90.dp).clip(CircleShape).background(AppColors.Surface).border(3.dp, AppColors.Accent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(user.nombre.take(1).uppercase().ifEmpty { "?" }, color = AppColors.Accent, fontSize = 38.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(Modifier.height(14.dp))
            Text(user.nombre, color = AppColors.Text, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(10.dp))
            Row(
                Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(rolColor.copy(alpha = 0.07f))
                    .border(1.dp, rolColor.copy(alpha = 0.27f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(if (isDocente) Icons.Filled.Work else Icons.Filled.School, null, tint = rolColor, modifier = Modifier.size(14.dp))
                Text(rolLabel, color = rolColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            Modifier.fillMaxWidth().clip(cardShape).background(AppColors.Surface).border(1.dp, AppColors.Border, cardShape).padding(20.dp),
        ) {
            InfoRow(Icons.Outlined.Email, "Correo electrónico", user.email)
            Box(Modifier.padding(vertical = 14.dp).fillMaxWidth().height(1.dp).background(AppColors.Border))
            InfoRow(if (isDocente) Icons.Outlined.Work else Icons.Outlined.School, "Rol", rolLabel)
            Box(Modifier.padding(vertical = 14.dp).fillMaxWidth().height(1.dp).background(AppColors.Border))
            InfoRow(Icons.Outlined.Fingerprint, "ID de usuario", "#${user.id}")
        }
        Spacer(Modifier.height(16.dp))

        val perms = listOf(
            Perm(Icons.Outlined.RemoveRedEye, "Ver y explorar recursos", true),
            Perm(Icons.Outlined.Search, "Buscar y filtrar", true),
            Perm(Icons.Outlined.FavoriteBorder, "Guardar en favoritos", !isDocente),
            Perm(Icons.Outlined.StarOutline, "Calificar recursos", !isDocente),
            Perm(Icons.Outlined.AddCircleOutline, "Crear recursos", isDocente),
            Perm(Icons.Outlined.Edit, "Editar recursos", isDocente),
            Perm(Icons.Outlined.DeleteOutline, "Eliminar recursos", isDocente),
        )
        Column(
            Modifier.fillMaxWidth().clip(cardShape).background(AppColors.Surface).border(1.dp, AppColors.Border, cardShape).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("PERMISOS DE TU CUENTA", color = AppColors.Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
            Spacer(Modifier.height(2.dp))
            perms.forEach { p ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(p.icon, null, tint = if (p.ok) AppColors.Accent else AppColors.Border, modifier = Modifier.size(16.dp))
                    Text(p.text, color = if (p.ok) AppColors.Text else AppColors.Dimmer, fontSize = 14.sp, modifier = Modifier.weight(1f))
                    Icon(
                        if (p.ok) Icons.Filled.CheckCircle else Icons.Filled.Cancel, null,
                        tint = if (p.ok) AppColors.Accent else AppColors.Dimmer, modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
        Spacer(Modifier.height(24.dp))

        val logoutShape = RoundedCornerShape(14.dp)
        Row(
            Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(logoutShape)
                .background(AppColors.Red.copy(alpha = 0.07f))
                .border(BorderStroke(1.5.dp, AppColors.Red.copy(alpha = 0.27f)), logoutShape)
                .clickable { confirm = true },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, null, tint = AppColors.Red, modifier = Modifier.size(20.dp))
            Spacer(Modifier.size(10.dp))
            Text("Cerrar Sesión", color = AppColors.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(Modifier.height(24.dp))
    }

    if (confirm) {
        MessageDialog(
            title = "Cerrar sesión",
            message = "¿Estás seguro de que deseas salir?",
            confirmText = "Salir",
            dismissText = "Cancelar",
            destructive = true,
            onDismiss = { confirm = false },
            onConfirm = { confirm = false; onLogout() },
        )
    }
}
