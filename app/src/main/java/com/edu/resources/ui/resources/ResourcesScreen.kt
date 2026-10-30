package com.edu.resources.ui.resources

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edu.resources.data.Recurso
import com.edu.resources.data.ResourcesViewModel
import com.edu.resources.data.SessionViewModel
import com.edu.resources.ui.components.Chip
import com.edu.resources.ui.components.LoadingBox
import com.edu.resources.ui.components.MessageDialog
import com.edu.resources.ui.components.ResourceImage
import com.edu.resources.ui.components.StarsRow
import com.edu.resources.ui.components.TypeBadge
import com.edu.resources.ui.theme.AppColors
import kotlinx.coroutines.launch

private val FILTROS = listOf("Todos", "libro", "video", "artículo", "tutorial", "curso")

fun filterRecursos(data: List<Recurso>, search: String, tipo: String): List<Recurso> {
    var result = data
    if (tipo != "Todos") result = result.filter { it.tipo?.lowercase() == tipo.lowercase() }
    val term = search.trim().lowercase()
    if (term.isNotEmpty()) {
        result = result.filter {
            it.titulo?.lowercase()?.contains(term) == true ||
                it.tipo?.lowercase()?.contains(term) == true ||
                it.id == term
        }
    }
    return result
}

@Composable
fun SearchBox(value: String, onChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(shape)
            .background(AppColors.Surface)
            .border(1.dp, AppColors.Border, shape)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(Icons.Outlined.Search, null, tint = AppColors.Dim, modifier = Modifier.size(18.dp))
        Box(Modifier.weight(1f)) {
            if (value.isEmpty()) Text("Buscar por ID, título o tipo...", color = AppColors.Dimmer, fontSize = 14.sp)
            BasicTextField(
                value = value,
                onValueChange = onChange,
                singleLine = true,
                textStyle = TextStyle(color = AppColors.Text, fontSize = 14.sp),
                cursorBrush = SolidColor(AppColors.Accent),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (value.isNotEmpty()) {
            Icon(
                Icons.Filled.Cancel, null, tint = AppColors.Dim,
                modifier = Modifier.size(18.dp).clickable { onChange("") },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesScreen(vm: ResourcesViewModel, session: SessionViewModel, onOpen: (String) -> Unit) {
    val items by vm.items.collectAsState()
    val loading by vm.loading.collectAsState()
    val refreshing by vm.refreshing.collectAsState()
    val error by vm.error.collectAsState()
    val user by session.user.collectAsState()
    val favs by session.favorites.collectAsState()

    var search by rememberSaveable { mutableStateOf("") }
    var tipo by rememberSaveable { mutableStateOf("Todos") }
    val filtered = remember(items, search, tipo) { filterRecursos(items, search, tipo) }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val showButtons by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 100 }
    }

    if (loading) {
        LoadingBox("Cargando recursos...")
        return
    }

    Box(Modifier.fillMaxSize().background(AppColors.Bg)) {
        PullToRefreshBox(isRefreshing = refreshing, onRefresh = { vm.refresh() }) {
            LazyColumn(state = listState, contentPadding = PaddingValues(bottom = 24.dp), modifier = Modifier.fillMaxSize()) {
                item {
                    Column(Modifier.statusBarsPadding().padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 16.dp)) {
                        Text("Recursos", color = AppColors.Text, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                        Text("${filtered.size} disponibles", color = AppColors.Dim, fontSize = 13.sp)
                        Spacer(Modifier.height(14.dp))
                        SearchBox(value = search, onChange = { search = it })
                    }
                    Row(
                        Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp).padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        FILTROS.forEach { f -> Chip(f, tipo == f) { tipo = f } }
                    }
                }
                if (filtered.isEmpty()) {
                    item {
                        Column(Modifier.fillMaxWidth().padding(top = 60.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.Search, null, tint = AppColors.Border, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("No se encontraron recursos", color = AppColors.Dimmer, fontSize = 15.sp)
                        }
                    }
                }
                items(filtered) { item ->
                    ResourceCard(
                        item = item,
                        isFav = favs.any { it.id == item.id },
                        showFav = user?.isEstudiante == true,
                        onToggleFav = { session.toggleFavorite(item) },
                        onClick = { onOpen(item.id) },
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showButtons,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                ScrollButton(Icons.Filled.KeyboardArrowUp, listState.canScrollBackward) {
                    scope.launch { listState.animateScrollToItem(0) }
                }
                ScrollButton(Icons.Filled.KeyboardArrowDown, listState.canScrollForward) {
                    scope.launch { listState.animateScrollToItem((listState.layoutInfo.totalItemsCount - 1).coerceAtLeast(0)) }
                }
            }
        }
    }

    if (error != null) {
        MessageDialog("Error", error ?: "", onDismiss = { vm.clearError() })
    }
}

@Composable
private fun ScrollButton(icon: androidx.compose.ui.graphics.vector.ImageVector, enabled: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (enabled) AppColors.Surface else AppColors.Bg)
            .border(1.dp, if (enabled) AppColors.Border else AppColors.Surface, CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, null, tint = if (enabled) AppColors.Accent else AppColors.Border, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun ResourceCard(
    item: Recurso,
    isFav: Boolean,
    showFav: Boolean,
    onToggleFav: () -> Unit,
    onClick: () -> Unit,
) {
    val promedio = item.promedio ?: 0.0
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        border = BorderStroke(1.dp, AppColors.Border),
    ) {
        Box {
            ResourceImage(item.imagen, Modifier.fillMaxWidth().height(150.dp))
            TypeBadge(item.tipo, Modifier.align(Alignment.BottomStart).padding(start = 14.dp, bottom = 10.dp))
        }
        Column(Modifier.padding(16.dp)) {
            Text(
                item.titulo.orEmpty(), color = AppColors.Text, fontSize = 17.sp, fontWeight = FontWeight.Bold,
                maxLines = 2, overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                item.descripcion.orEmpty(), color = AppColors.Muted, fontSize = 13.sp, lineHeight = 18.sp,
                maxLines = 2, overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StarsRow(promedio)
                    Spacer(Modifier.size(4.dp))
                    Text(if (promedio > 0) String.format("%.1f", promedio) else "—", color = AppColors.Muted, fontSize = 12.sp)
                }
                if (showFav) {
                    IconButton(onClick = onToggleFav, modifier = Modifier.size(32.dp)) {
                        Icon(
                            if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, null,
                            tint = if (isFav) AppColors.Pink else AppColors.Dimmer,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            }
        }
    }
}
