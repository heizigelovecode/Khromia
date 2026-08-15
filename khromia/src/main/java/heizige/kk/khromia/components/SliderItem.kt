package heizige.kk.khromia.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import heizige.kk.khromia.R
import heizige.kk.khromia.text.OptionText

/**
 * 与 [OptionItem] 同风格的滑条列表项：左侧图标 + 标题/副标题，下方 [FancySlider]。
 * 数值仅在松手时通过 [onValueChange] 提交；标题旁可显示跟手预览文案。
 */
@Composable
fun SliderItem(
    modifier: Modifier = Modifier,
    painter: Painter,
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    subtitle: String? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true,
    valueLabel: ((Float) -> String)? = null,
    defaultValue: Float? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.26f),
    shape: Shape = RoundedCornerShape(20.dp),
) {
    SliderItemLayout(
        modifier = modifier,
        icon = {
            Icon(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.54f),
                        CircleShape
                    )
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.87f)
            )
        },
        title = title,
        subtitle = subtitle,
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        steps = steps,
        enabled = enabled,
        valueLabel = valueLabel,
        defaultValue = defaultValue,
        backgroundColor = backgroundColor,
        shape = shape,
    )
}

@Composable
fun SliderItem(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    subtitle: String? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true,
    valueLabel: ((Float) -> String)? = null,
    defaultValue: Float? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.26f),
    shape: Shape = RoundedCornerShape(20.dp),
) {
    SliderItemLayout(
        modifier = modifier,
        icon = {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.54f),
                        CircleShape
                    )
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.87f)
            )
        },
        title = title,
        subtitle = subtitle,
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        steps = steps,
        enabled = enabled,
        valueLabel = valueLabel,
        defaultValue = defaultValue,
        backgroundColor = backgroundColor,
        shape = shape,
    )
}

@Composable
private fun SliderItemLayout(
    modifier: Modifier,
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String?,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    enabled: Boolean,
    valueLabel: ((Float) -> String)?,
    defaultValue: Float?,
    backgroundColor: Color,
    shape: Shape,
) {
    var visualValue by remember { mutableFloatStateOf(value) }
    var showResetDialog by remember { mutableStateOf(false) }
    LaunchedEffect(value) { visualValue = value }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon()
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                OptionText(text = title)
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.54f),
                        fontSize = 14.sp
                    )
                }
            }
            if (valueLabel != null) {
                Spacer(modifier = Modifier.width(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = if (defaultValue != null) {
                        Modifier.bouncyClickable { showResetDialog = true }
                    } else {
                        Modifier
                    }
                ) {
                    Text(
                        text = valueLabel(visualValue),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        FancySlider(
            value = value,
            onValueChange = onValueChange,
            onVisualValueChange = { visualValue = it },
            valueRange = valueRange,
            steps = steps,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
        )
    }

    if (showResetDialog && defaultValue != null) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(stringResource(R.string.slider_reset_title)) },
            text = { Text(stringResource(R.string.slider_reset_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onValueChange(defaultValue)
                        showResetDialog = false
                    }
                ) { Text(stringResource(R.string.slider_reset_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(stringResource(R.string.slider_reset_cancel))
                }
            }
        )
    }
}
