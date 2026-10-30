package com.edu.resources.ui.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edu.resources.data.RecursoInput
import com.edu.resources.data.ResourcesViewModel
import com.edu.resources.data.TIPOS_RECURSO
import com.edu.resources.ui.components.AppTextField
import com.edu.resources.ui.components.MessageDialog
import com.edu.resources.ui.components.PrimaryButton
import com.edu.resources.ui.components.capitalized
import com.edu.resources.ui.theme.AppColors
import kotlinx.coroutines.launch

@Composable
private fun Label(text: String) {
    Text(text.uppercase(), color = AppColors.Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun FieldError(msg: String?) {
    if (msg != null) Text(msg, color = AppColors.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp))
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResourceFormScreen(id: String?, vm: ResourcesViewModel, onBack: () -> Unit) {
    val items by vm.items.collectAsState()
    val editing = remember(id) { id?.let { i -> items.find { it.id == i } } }
    val isEditing = editing != null

    var titulo by remember { mutableStateOf(editing?.titulo.orEmpty()) }
    var descripcion by remember { mutableStateOf(editing?.descripcion.orEmpty()) }
    var tipo by remember { mutableStateOf(editing?.tipo.orEmpty()) }
    var enlace by remember { mutableStateOf(editing?.enlace.orEmpty()) }
    var imagen by remember { mutableStateOf(editing?.imagen.orEmpty()) }
    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var loading by remember { mutableStateOf(false) }
    var dialog by remember { mutableStateOf<Pair<String, String>?>(null) }
    var success by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun validate(): Boolean {
        val e = mutableMapOf<String, String>()
        if (titulo.isBlank()) e["titulo"] = "El título es requerido"
        if (descripcion.isBlank()) e["descripcion"] = "La descripción es requerida"
        if (tipo.isEmpty()) e["tipo"] = "Selecciona un tipo"
        if (enlace.isBlank()) e["enlace"] = "El enlace es requerido"
        else if (!enlace.trim().startsWith("http")) e["enlace"] = "Debe ser una URL válida (http...)"
        if (imagen.isNotBlank() && !imagen.trim().startsWith("http")) e["imagen"] = "Debe ser una URL válida (http...)"
        errors = e
        return e.isEmpty()
    }

    fun submit() {
        if (!validate()) return
        loading = true
        val payload = RecursoInput(titulo.trim(), descripcion.trim(), tipo, enlace.trim(), imagen.trim())
        scope.launch {
            try {
                if (editing != null) {
                    vm.update(editing.id, payload)
                    success = "Recurso actualizado correctamente."
                } else {
                    vm.create(payload)
                    success = "Recurso creado correctamente."
                }
            } catch (e: Exception) {
                dialog = "Error" to "No se pudo guardar el recurso."
            } finally {
                loading = false
            }
        }
    }

    Column(Modifier.fillMaxSize().background(AppColors.Bg).statusBarsPadding().imePadding()) {
        Row(
            Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(
                onClick = onBack,
                colors = IconButtonDefaults.iconButtonColors(containerColor = AppColors.Surface),
                modifier = Modifier.size(38.dp),
            ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = AppColors.Text, modifier = Modifier.size(22.dp)) }
            Text(if (isEditing) "Editar Recurso" else "Nuevo Recurso", color = AppColors.Text, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(38.dp))
        }

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(start = 16.dp, end = 16.dp, bottom = 40.dp)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(AppColors.Surface)
                    .border(1.dp, AppColors.Border, RoundedCornerShape(20.dp))
                    .padding(20.dp),
            ) {
                Label("Título *")
                AppTextField(titulo, { titulo = it }, placeholder = "Nombre del recurso educativo", isError = "titulo" in errors,
                    capitalization = KeyboardCapitalization.Sentences)
                FieldError(errors["titulo"])
                Spacer(Modifier.height(20.dp))

                Label("Descripción *")
                AppTextField(descripcion, { descripcion = it }, placeholder = "Descripción detallada del recurso...",
                    singleLine = false, minLines = 4, isError = "descripcion" in errors,
                    capitalization = KeyboardCapitalization.Sentences)
                FieldError(errors["descripcion"])
                Spacer(Modifier.height(20.dp))

                Label("Tipo *")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TIPOS_RECURSO.forEach { t ->
                        val active = tipo == t
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (active) AppColors.Accent else AppColors.Bg)
                                .border(1.5.dp, if (active) AppColors.Accent else AppColors.Border, RoundedCornerShape(20.dp))
                                .clickable { tipo = t }
                                .padding(horizontal = 16.dp, vertical = 9.dp),
                        ) {
                            Text(t.capitalized(), color = if (active) AppColors.Bg else AppColors.Dim, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                FieldError(errors["tipo"])
                Spacer(Modifier.height(20.dp))

                Label("Enlace / URL *")
                AppTextField(enlace, { enlace = it }, placeholder = "https://...", keyboardType = KeyboardType.Uri, isError = "enlace" in errors)
                FieldError(errors["enlace"])
                Spacer(Modifier.height(20.dp))

                Label("Imagen (URL)")
                AppTextField(imagen, { imagen = it }, placeholder = "https://... (opcional)", keyboardType = KeyboardType.Uri, isError = "imagen" in errors)
                FieldError(errors["imagen"])
                Spacer(Modifier.height(28.dp))

                PrimaryButton(
                    text = if (isEditing) "Guardar cambios" else "Crear recurso",
                    onClick = ::submit,
                    loading = loading,
                    icon = if (isEditing) Icons.Filled.CheckCircle else Icons.Filled.AddCircle,
                )
            }
        }
    }

    dialog?.let { (t, m) -> MessageDialog(t, m, onDismiss = { dialog = null }) }
    success?.let { msg -> MessageDialog("¡Éxito!", msg, onDismiss = { success = null; onBack() }) }
}
