package com.edu.resources.ui.resources

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edu.resources.data.ResourcesViewModel
import com.edu.resources.data.SessionViewModel
import com.edu.resources.data.User
import com.edu.resources.ui.components.MessageDialog
import com.edu.resources.ui.components.ResourceImage
import com.edu.resources.ui.components.StarsRow
import com.edu.resources.ui.components.tipoColor
import com.edu.resources.ui.theme.AppColors
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(
    id: String,
    vm: ResourcesViewModel,
    session: SessionViewModel,
    user: User,
    onBack: () -> Unit,
) {
    val items by vm.items.collectAsState()
    val favs by session.favorites.collectAsState()
    val recurso = items.find { it.id == id } ?: favs.find { it.id == id }

    var userRating by remember { mutableIntStateOf(0) }
    var ratingLoading by remember { mutableStateOf(false) }
    var dialog by remember { mutableStateOf<Pair<String, String>?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    if (recurso == null) {
        Column(
            Modifier.fillMaxSize().background(AppColors.Bg).statusBarsPadding().padding(20.dp),
        ) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AppColors.Text) }
            Text("Recurso no encontrado", color = AppColors.Muted, modifier = Modifier.padding(16.dp))
        }
        return
    }

    val isStudent = user.isEstudiante
    val fav = favs.any { it.id == recurso.id }
    val color = tipoColor(recurso.tipo)
    val promedio = recurso.promedio ?: 0.0

    Column(Modifier.fillMaxSize().background(AppColors.Bg).verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth().height(280.dp)) {
            ResourceImage(recurso.imagen, Modifier.fillMaxSize())
            val overlay = IconButtonDefaults.iconButtonColors(containerColor = AppColors.Bg.copy(alpha = 0.7f))
            IconButton(
                onClick = onBack,
                colors = overlay,
                modifier = Modifier.align(Alignment.TopStart).statusBarsPadding().padding(start = 20.dp, top = 8.dp),
            ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = AppColors.Text) }
            if (isStudent) {
                IconButton(
                    onClick = { session.toggleFavorite(recurso) },
                    colors = overlay,
                    modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(end = 20.dp, top = 8.dp),
                ) {
                    Icon(
                        if (fav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, "Favorito",
                        tint = if (fav) AppColors.Pink else AppColors.Text,
                    )
                }
            }
        }

        Column(
            Modifier
                .offset(y = (-28).dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(AppColors.Bg)
                .padding(24.dp),
        ) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(color.copy(alpha = 0.13f))
                    .padding(horizontal = 12.dp, vertical = 5.dp),
            ) {
                Text((recurso.tipo ?: "").uppercase(), color = color, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
            }
            Spacer(Modifier.height(12.dp))
            Text(recurso.titulo.orEmpty(), color = AppColors.Text, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                StarsRow(promedio, 18.dp)
                Spacer(Modifier.size(6.dp))
                Text(
                    if (promedio > 0) String.format("%.1f / 5", promedio) else "Sin calificaciones",
                    color = AppColors.Muted, fontSize = 13.sp,
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(recurso.descripcion.orEmpty(), color = AppColors.Muted, fontSize = 15.sp, lineHeight = 24.sp)
            Spacer(Modifier.height(24.dp))

            if (!recurso.enlace.isNullOrBlank()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppColors.Accent)
                        .clickable {
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(recurso.enlace)))
                            } catch (e: ActivityNotFoundException) {
                                dialog = "Error" to "No se pudo abrir el enlace."
                            }
                        }
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, null, tint = AppColors.Bg, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(8.dp))
                    Text("Acceder al recurso", color = AppColors.Bg, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(Modifier.height(24.dp))
            }

            if (isStudent) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                    border = BorderStroke(1.dp, AppColors.Border),
                ) {
                    Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Column(Modifier.fillMaxWidth()) {
                            Text("Tu calificación", color = AppColors.Text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text("Ayuda a otros estudiantes calificando este recurso", color = AppColors.Dim, fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            (1..5).forEach { s ->
                                Icon(
                                    if (s <= userRating) Icons.Filled.Star else Icons.Filled.StarBorder, "$s estrellas",
                                    tint = if (s <= userRating) AppColors.Yellow else AppColors.Border,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clickable(enabled = !ratingLoading) {
                                            userRating = s
                                            ratingLoading = true
                                            scope.launch {
                                                try {
                                                    val updated = vm.rate(recurso, s)
                                                    session.refreshFavorite(updated)
                                                } catch (e: Exception) {
                                                    dialog = "Error" to "No se pudo guardar la calificación."
                                                } finally {
                                                    ratingLoading = false
                                                }
                                            }
                                        },
                                )
                            }
                        }
                        if (ratingLoading) {
                            Text("Guardando...", color = AppColors.Accent, fontSize = 12.sp, modifier = Modifier.padding(top = 10.dp))
                        } else if (userRating > 0) {
                            Text(
                                "✓ Calificaste con $userRating estrella${if (userRating != 1) "s" else ""}",
                                color = AppColors.Accent, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 10.dp),
                            )
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                border = BorderStroke(1.dp, AppColors.Border),
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Outlined.LocalOffer, null, tint = AppColors.Dim, modifier = Modifier.size(16.dp))
                    Text("ID del recurso:", color = AppColors.Dim, fontSize = 13.sp)
                    Text("#${recurso.id}", color = AppColors.Muted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    dialog?.let { (t, m) -> MessageDialog(t, m, onDismiss = { dialog = null }) }
}
