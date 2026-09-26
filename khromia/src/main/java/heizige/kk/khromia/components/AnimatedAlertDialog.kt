package heizige.kk.khromia.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

/**
 * Material 3 风格的警示弹窗，宿主为官方 [androidx.compose.ui.window.Dialog]
 * （见 [AnimatedDialogWindow]）。
 *
 * 保持挂载并用 [visible] 驱动；[onDismissRequest] 在退场动画结束后回调。
 */
@Composable
fun AnimatedAlertDialog(
    visible: Boolean = true,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    properties: DialogProperties = DialogProperties(),
    enablePredictiveBack: Boolean = true
) {
    AnimatedDialogWindow(
        visible = visible,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        properties = properties,
        enablePredictiveBack = enablePredictiveBack,
    ) {
        AlertDialogSurface(
            confirmButton = confirmButton,
            dismissButton = dismissButton,
            icon = icon,
            title = title,
            text = text,
            modifier = Modifier,
            shape = shape,
            containerColor = containerColor,
            iconContentColor = iconContentColor,
            titleContentColor = titleContentColor,
            textContentColor = textContentColor,
            tonalElevation = tonalElevation
        )
    }
}

@Composable
private fun AlertDialogSurface(
    confirmButton: @Composable () -> Unit,
    dismissButton: (@Composable () -> Unit)?,
    icon: (@Composable () -> Unit)?,
    title: (@Composable () -> Unit)?,
    text: (@Composable () -> Unit)?,
    modifier: Modifier,
    shape: Shape,
    containerColor: Color,
    iconContentColor: Color,
    titleContentColor: Color,
    textContentColor: Color,
    tonalElevation: Dp
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .sizeIn(minWidth = 280.dp, maxWidth = 560.dp),
        shape = shape,
        color = containerColor,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = tonalElevation
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            icon?.let { content ->
                CompositionLocalProvider(LocalContentColor provides iconContentColor) {
                    content()
                }
                if (title != null || text != null) {
                    Spacer(Modifier.height(16.dp))
                }
            }
            title?.let { content ->
                CompositionLocalProvider(LocalContentColor provides titleContentColor) {
                    content()
                }
                if (text != null) {
                    Spacer(Modifier.height(16.dp))
                }
            }
            text?.let { content ->
                CompositionLocalProvider(LocalContentColor provides textContentColor) {
                    content()
                }
            }
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                dismissButton?.invoke()
                confirmButton()
            }
        }
    }
}
