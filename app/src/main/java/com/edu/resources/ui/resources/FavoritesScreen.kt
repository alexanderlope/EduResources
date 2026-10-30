package com.edu.resources.ui.resources

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edu.resources.data.SessionViewModel
import com.edu.resources.ui.components.ResourceImage
import com.edu.resources.ui.components.tipoColor
import com.edu.resources.ui.theme.AppColors

@Composable
fun FavoritesScreen(session: SessionViewModel, onOpen: (String) -> Unit) {
    val favorites by session.favorites.collectAsState()

    Column(Modifier.fillMaxSize().background(AppColors.Bg)) {
        Column(Modifier.statusBarsPadding().padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 20.dp)) {
            Text("Mis Favoritos", color = AppColors.Text, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(4.dp))
            Text("${favorites.size} recursos guardados", color = AppColors.Dim, fontSize = 13.sp)
        }

        if (favorites.isEmpty()) {
            Column(
                Modifier.fillMaxSize().padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(Icons.Outlined.FavoriteBorder, null, tint = AppColors.Border, modifier = Modifier.size(64.dp))
                Spacer(Modifier.height(16.dp))
                Text("Sin favoritos aún", color = AppColors.Text, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Explora los recursos y guarda los que más te interesen",
                    color = AppColors.Dim, fontSize = 14.sp, lineHeight = 22.sp, textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items(favorites) { item ->
                    val color = tipoColor(item.tipo)
                    Card(
                        onClick = { onOpen(item.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                        border = BorderStroke(1.dp, AppColors.Border),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ResourceImage(item.imagen, Modifier.size(90.dp))
                            Column(Modifier.weight(1f).padding(14.dp)) {
                                Box(
                                    Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(color.copy(alpha = 0.13f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp),
                                ) {
                                    Text((item.tipo ?: "").uppercase(), color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(item.titulo.orEmpty(), color = AppColors.Text, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Spacer(Modifier.height(4.dp))
                                Text(item.descripcion.orEmpty(), color = AppColors.Dim, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            IconButton(onClick = { session.toggleFavorite(item) }) {
                                Icon(Icons.Filled.Favorite, "Quitar de favoritos", tint = AppColors.Pink)
                            }
                        }
                    }
                }
            }
        }
    }
}
