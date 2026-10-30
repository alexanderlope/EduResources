package com.edu.resources.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.edu.resources.ui.theme.AppColors
import kotlin.math.roundToInt

fun tipoColor(tipo: String?): Color = when (tipo?.lowercase()) {
    "libro" -> Color(0xFF818CF8)
    "video" -> Color(0xFFF472B6)
    "artículo" -> Color(0xFF34D399)
    "tutorial" -> Color(0xFFFBBF24)
    "curso" -> Color(0xFF60A5FA)
    else -> AppColors.Accent
}

fun tipoIcon(tipo: String?): ImageVector = when (tipo?.lowercase()) {
    "libro" -> Icons.Outlined.MenuBook
    "video" -> Icons.Outlined.Videocam
    "artículo" -> Icons.Outlined.Description
    "tutorial" -> Icons.Outlined.Code
    "curso" -> Icons.Outlined.School
    else -> Icons.Outlined.Description
}

fun String.capitalized(): String = replaceFirstChar { it.uppercase() }

@Composable
fun LoadingBox(text: String?) {
    Column(
        Modifier.fillMaxSize().background(AppColors.Bg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(color = AppColors.Accent)
        if (text != null) {
            Spacer(Modifier.height(12.dp))
            Text(text, color = AppColors.Accent, fontSize = 14.sp)
        }
    }
}

@Composable
fun StarsRow(promedio: Double, size: Dp = 13.dp) {
    Row {
        repeat(5) { i ->
            Icon(
                imageVector = if (i + 1 <= promedio.roundToInt()) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = null,
                tint = AppColors.Yellow,
                modifier = Modifier.size(size),
            )
        }
    }
}

@Composable
fun ResourceImage(url: String?, modifier: Modifier = Modifier) {
    if (url.isNullOrBlank()) {
        Box(modifier.background(AppColors.Surface), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Description, null, tint = AppColors.Border, modifier = Modifier.size(40.dp))
        }
    } else {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = modifier.background(AppColors.Surface),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun TypeBadge(tipo: String?, modifier: Modifier = Modifier) {
    val color = tipoColor(tipo)
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier
            .clip(shape)
            .background(AppColors.Bg.copy(alpha = 0.8f))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Icon(tipoIcon(tipo), null, tint = color, modifier = Modifier.size(13.dp))
        Text((tipo ?: "").uppercase(), color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun Chip(text: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        Modifier
            .clip(shape)
            .background(if (selected) AppColors.Accent else AppColors.Surface)
            .border(1.dp, if (selected) AppColors.Accent else AppColors.Border, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
    ) {
        Text(
            text,
            color = if (selected) AppColors.Bg else AppColors.Muted,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailing: (@Composable () -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    isError: Boolean = false,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = AppColors.Dimmer) },
        leadingIcon = leadingIcon?.let { icon -> @Composable { Icon(icon, null, tint = AppColors.Dim) } },
        trailingIcon = trailing,
        singleLine = singleLine,
        minLines = minLines,
        isError = isError,
        visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, capitalization = capitalization),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = AppColors.Bg,
            unfocusedContainerColor = AppColors.Bg,
            errorContainerColor = AppColors.Bg,
            focusedBorderColor = AppColors.Accent,
            unfocusedBorderColor = AppColors.Border,
            errorBorderColor = AppColors.Red,
            focusedTextColor = AppColors.Text,
            unfocusedTextColor = AppColors.Text,
            cursorColor = AppColors.Accent,
        ),
    )
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    icon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        enabled = !loading,
        modifier = modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.Accent,
            contentColor = AppColors.Bg,
            disabledContainerColor = AppColors.Accent.copy(alpha = 0.6f),
            disabledContentColor = AppColors.Bg,
        ),
    ) {
        if (loading) {
            CircularProgressIndicator(Modifier.size(22.dp), color = AppColors.Bg, strokeWidth = 2.dp)
        } else {
            if (icon != null) {
                Icon(icon, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(text, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }
    }
}

@Composable
fun MessageDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    confirmText: String = "OK",
    dismissText: String? = null,
    destructive: Boolean = false,
    onConfirm: () -> Unit = onDismiss,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppColors.Surface,
        titleContentColor = AppColors.Text,
        textContentColor = AppColors.Muted,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText, color = if (destructive) AppColors.Red else AppColors.Accent, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = dismissText?.let { label ->
            @Composable { TextButton(onClick = onDismiss) { Text(label, color = AppColors.Muted) } }
        },
    )
}

val CardBorder = BorderStroke(1.dp, AppColors.Border)
