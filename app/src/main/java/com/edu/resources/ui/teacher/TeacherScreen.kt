package com.edu.resources.ui.teacher

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edu.resources.data.Recurso
import com.edu.resources.data.ResourcesViewModel
import com.edu.resources.ui.components.LoadingBox
import com.edu.resources.ui.components.MessageDialog
import com.edu.resources.ui.components.capitalized
import com.edu.resources.ui.components.tipoColor
import com.edu.resources.ui.resources.SearchBox
import com.edu.resources.ui.resources.filterRecursos
import com.edu.resources.ui.theme.AppColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherScreen(vm: ResourcesViewModel, onAdd: () -> Unit, onEdit: (String) -> Unit) {
    val items by vm.items.collectAsState()
    val loading by vm.loading.collectAsState()
    val refreshing by vm.refreshing.collectAsState()
    val error by vm.error.collectAsState()

    var search by rememberSaveable { mutableStateOf("") }
    var toDelete by remember { mutableStateOf<Recurso?>(null) }
    var deleteError by remember { mutableStateOf(false) }
    val filtered = remember(items, search) { filterRecursos(items, search, "Todos") }
    val scope = rememberCoroutineScope()

    if (loading) {
        LoadingBox("Cargando recursos...")
        return
    }

    Column(Modifier.fillMaxSize().background(AppColors.Bg)) {
        Column(Modifier.statusBarsPadding().padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Gestión", color = AppColors.Text, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Panel del Docente", color = AppColors.Dim, fontSize = 13.sp)
                }
                Box(
                    Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(AppColors.Accent).clickable(onClick = onAdd),
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Filled.Add, "Agregar recurso", tint = AppColors.Bg, modifier = Modifier.size(24.dp)) }
            }
            Spacer(Modifier.height(16.dp))
            SearchBox(value = search, onChange = { search = it })
            Spacer(Modifier.height(10.dp))
            Text("${filtered.size} recursos", color = AppColors.Dimmer, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }

        PullToRefreshBox(isRefreshing = refreshing, onRefresh = { vm.refresh() }, modifier = Modifier.weight(1f)) {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                if (filtered.isEmpty()) {
                    item {
                        Column(Modifier.fillMaxWidth().padding(top = 60.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.Description, null, tint = AppColors.Border, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("No hay recursos", color = AppColors.Dimmer, fontSize = 16.sp)
                            Text(
                                "+ Agregar primer recurso", color = AppColors.Accent, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 16.dp).clickable(onClick = onAdd),
                            )
                        }
                    }
                }
                items(filtered) { item ->
                    val color = tipoColor(item.tipo)
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                        border = BorderStroke(1.dp, AppColors.Border),
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(Modifier.size(10.dp).clip(CircleShape).background(color))
                            Column(Modifier.weight(1f)) {
                                Text(item.titulo.orEmpty(), color = AppColors.Text, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text((item.tipo ?: "").capitalized(), color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("ID: ${item.id}", color = AppColors.Dimmer, fontSize = 11.sp)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(AppColors.Accent.copy(alpha = 0.13f)).clickable { onEdit(item.id) },
                                    contentAlignment = Alignment.Center,
                                ) { Icon(Icons.Filled.Edit, "Editar", tint = AppColors.Accent, modifier = Modifier.size(16.dp)) }
                                Box(
                                    Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(AppColors.Red.copy(alpha = 0.13f)).clickable { toDelete = item },
                                    contentAlignment = Alignment.Center,
                                ) { Icon(Icons.Filled.Delete, "Eliminar", tint = AppColors.Red, modifier = Modifier.size(16.dp)) }
                            }
                        }
                    }
                }
            }
        }
    }

    toDelete?.let { r ->
        MessageDialog(
            title = "Eliminar recurso",
            message = "¿Estás seguro de eliminar \"${r.titulo}\"? Esta acción no se puede deshacer.",
            confirmText = "Eliminar",
            dismissText = "Cancelar",
            destructive = true,
            onDismiss = { toDelete = null },
            onConfirm = {
                toDelete = null
                scope.launch {
                    try { vm.delete(r.id) } catch (e: Exception) { deleteError = true }
                }
            },
        )
    }
    if (deleteError) MessageDialog("Error", "No se pudo eliminar el recurso.", onDismiss = { deleteError = false })
    if (error != null) MessageDialog("Error", error ?: "", onDismiss = { vm.clearError() })
}
