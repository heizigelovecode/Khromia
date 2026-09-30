package heizige.kk.khromia.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

/**
 * Material 3 风格的警示弹窗，宿主为官方 [androidx.compose.ui.window.Dialog]
 * （见 [AnimatedDialogWindow]）。
 *
 * 内容布局与官方 `AlertDialog` 对齐（`DialogTokens`：28dp 圆角 / `surfaceContainerHigh` /
 * `headlineSmall` 标题 / `bodyMedium` 正文 / `labelLarge` 按钮 / 8dp 按钮间距）。
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
        properties = properties,
        enablePredictiveBack = enablePredictiveBack,
    ) {
        AlertDialogSurface(
            confirmButton = confirmButton,
            dismissButton = dismissButton,
            icon = icon,
            title = title,
            text = text,
            modifier = modifier,
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
    Box(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .sizeIn(minWidth = 280.dp, maxWidth = 560.dp),
        propagateMinConstraints = true
    ) {
        Surface(
            shape = shape,
            color = containerColor,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = tonalElevation
        ) {
            Column(modifier = Modifier.padding(DialogPadding)) {
                icon?.let { content ->
                    CompositionLocalProvider(LocalContentColor provides iconContentColor) {
                        Box(
                            modifier = Modifier
                                .padding(IconPadding)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            content()
                        }
                    }
                }
                title?.let { content ->
                    ProvideContentColorTextStyle(
                        contentColor = titleContentColor,
                        textStyle = MaterialTheme.typography.headlineSmall
                    ) {
                        Box(
                            // 有 icon 时标题居中，否则按 M3 规范左对齐。
                            modifier = Modifier
                                .padding(TitlePadding)
                                .align(
                                    if (icon == null) {
                                        Alignment.Start
                                    } else {
                                        Alignment.CenterHorizontally
                                    }
                                )
                        ) {
                            content()
                        }
                    }
                }
                text?.let { content ->
                    ProvideContentColorTextStyle(
                        contentColor = textContentColor,
                        textStyle = MaterialTheme.typography.bodyMedium
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(weight = 1f, fill = false)
                                .padding(TextPadding)
                                .align(Alignment.Start)
                        ) {
                            content()
                        }
                    }
                }
                Box(modifier = Modifier.align(Alignment.End)) {
                    ProvideContentColorTextStyle(
                        contentColor = MaterialTheme.colorScheme.primary,
                        textStyle = MaterialTheme.typography.labelLarge
                    ) {
                        AlertDialogButtons(confirmButton = confirmButton, dismissButton = dismissButton)
                    }
                }
            }
        }
    }
}

/** 官方 `AlertDialogContent` 同款按钮布局：横向排布，空间不足时换行，间距 8dp。 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AlertDialogButtons(
    confirmButton: @Composable () -> Unit,
    dismissButton: (@Composable () -> Unit)?
) {
    val originalLayoutDirection = LocalLayoutDirection.current
    // 竖排时确认在前、横排时确认在后：翻转方向让 FlowRow 自然排出该顺序。
    CompositionLocalProvider(LocalLayoutDirection provides originalLayoutDirection.flip()) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(ButtonsMainAxisSpacing),
            verticalArrangement = Arrangement.spacedBy(ButtonsCrossAxisSpacing)
        ) {
            CompositionLocalProvider(
                LocalLayoutDirection provides originalLayoutDirection,
                content = {
                    confirmButton()
                    dismissButton?.invoke()
                }
            )
        }
    }
}

@Composable
private fun ProvideContentColorTextStyle(
    contentColor: Color,
    textStyle: TextStyle,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalTextStyle provides textStyle,
        content = content
    )
}

private fun LayoutDirection.flip(): LayoutDirection = when (this) {
    LayoutDirection.Ltr -> LayoutDirection.Rtl
    LayoutDirection.Rtl -> LayoutDirection.Ltr
}

private val IconPadding = PaddingValues(bottom = 16.dp)
private val TitlePadding = PaddingValues(bottom = 16.dp)
private val DialogPadding = PaddingValues(all = 24.dp)
private val TextPadding = PaddingValues(bottom = 24.dp)
private val ButtonsMainAxisSpacing = 8.dp
private val ButtonsCrossAxisSpacing = 8.dp
