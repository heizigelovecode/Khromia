package heizige.kk.khromia.components

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import heizige.kk.khromia.R
import heizige.kk.khromia.helper.PredictiveBackHandler
import heizige.kk.khromia.layout.FullscreenPopup
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

data class EditFieldConfig(
    val label: String,
    val initialValue: String = "",
    val placeholder: String = "",
    val keyboardType: KeyboardType = KeyboardType.Text,
    val range: ClosedRange<Double>? = null,
    val maxLength: Int? = null,
    val onValidate: ((String) -> String?)? = null
)

/**
 * [EditFieldConfig] 列表的逐字段校验，纯函数、无 Composable 依赖。
 *
 * 单独抽出来是为了让 Miuix 分支（`KedgeEditDialog` → `MiuixEditDialog`）能共用同一份
 * 规则。两边各写一遍的话，修一个校验 bug 要改两处，很容易漏。
 *
 * 错误文案由调用方以参数传入（原本是 `stringResource` 的结果，抽成纯函数后不能再
 * 捕获 Composable 作用域）。
 *
 * @return 与 [fields] 等长的列表，元素为该字段的错误文案，`null` 表示通过。
 */
fun validateEditFields(
    fields: List<EditFieldConfig>,
    values: List<String>,
    invalidNumberError: String,
    rangeErrorTemplate: String,
    maxLengthErrorTemplate: String,
): List<String?> = fields.mapIndexed { index, config ->
    val value = values.getOrElse(index) { "" }

    if (config.keyboardType == KeyboardType.Number || config.keyboardType == KeyboardType.Decimal) {
        val num = value.toDoubleOrNull()
        if (value.isNotEmpty() && num == null) {
            return@mapIndexed invalidNumberError
        }
        if (num != null && config.range != null) {
            if (num !in config.range) {
                return@mapIndexed rangeErrorTemplate.format(config.range.start, config.range.endInclusive)
            }
        }
    }

    if (config.maxLength != null && value.length > config.maxLength) {
        return@mapIndexed maxLengthErrorTemplate.format(config.maxLength)
    }

    config.onValidate?.invoke(value)
}


/**
 * 多字段编辑弹窗。
 *
 * **本组件是纯 MD3 实现**（28dp 圆角 + `OutlinedTextField`）。Miuix 分支由
 * `heizige.kk.kedge.overlays.KedgeEditDialog` 分发，不要往这里塞 Miuix 代码。
 *
 * [visible] 转 `false` 时会先播完退场动画（`fadeOut` + `scaleOut`）再退出组合，
 * 所以调用点应当**始终调用本组件并用 [visible] 控制显隐**，不要用
 * `if (show) EditDialog(visible = true)` —— 那样退场动画永远播不出来。
 *
 * [onDismiss] 在**退场动画播完之后**才回调，两种关闭路径都会走它：
 * 点取消 / 点 scrim / 系统返回手势（内部触发），以及调用点自己把 [visible] 置
 * `false`（此时内部仍会等退场结束再回调，便于调用方在同一点释放状态）。
 */
@Composable
fun EditDialog(
    visible: Boolean,
    title: String,
    fields: List<EditFieldConfig>,
    onDismiss: () -> Unit,
    onConfirm: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    confirmText: String? = null,
    dismissText: String? = null
) {
    // 退场动画播完才真正退出组合。不能像以前那样 `if (!visible) return` —— 那会在
    // visible 转 false 的瞬间把内容卸载，scaleOut/fadeOut 一帧都播不出来。
    // exitDone 只在「已经播完退场」后为 true，用于把组件彻底移出组合。
    var exitDone by remember { mutableStateOf(false) }
    LaunchedEffect(visible) { exitDone = false }
    if (!visible && exitDone) return

    val actualConfirmText = confirmText ?: stringResource(R.string.edit_dialog_confirm)
    val actualDismissText = dismissText ?: stringResource(R.string.edit_dialog_cancel)

    val errInvalidNumber = stringResource(R.string.edit_dialog_error_invalid_number)
    val errRangeTemplate = stringResource(R.string.edit_dialog_error_range)
    val errMaxLengthTemplate = stringResource(R.string.edit_dialog_error_max_length)

    // 刻意**不** key 在 fields 上。fields 是调用点每次重组新建的 list，其中
    // onValidate 是捕获 lambda、引用每次都变，于是 key 在 fields 上会让父层的任意
    // 一次重组都把用户已输入的内容清空（以前靠调用点用 `if (show)` 把组件整个卸载
    // 掩盖了这点；一旦改成用 visible 控制显隐、组件常驻就会暴露）。
    // 这里只跟 initialValue：切换到另一个初始值不同的对话框时仍能正确重置。
    val initialValues = remember(fields) { fields.map { it.initialValue } }
    val values = remember(visible, initialValues) {
        mutableStateListOf<String>().apply { addAll(initialValues) }
    }

    val errorMessages = validateEditFields(
        fields = fields,
        values = values,
        invalidNumberError = errInvalidNumber,
        rangeErrorTemplate = errRangeTemplate,
        maxLengthErrorTemplate = errMaxLengthTemplate,
    )

    val isAllValid = errorMessages.all { it == null }

    var isVisible by remember { mutableStateOf(false) }
    var shouldDismiss by remember { mutableStateOf(false) }
    var backProgress by remember { mutableFloatStateOf(0f) }
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val triggerDismiss = { shouldDismiss = true }

    // isVisible 由 visible 驱动：调用点把 visible 置 false 就走退场路径。
    LaunchedEffect(visible) {
        isVisible = visible
    }

    // 内部关闭（取消 / scrim / 返回手势）：先播退场，播完再通知调用方。
    LaunchedEffect(shouldDismiss) {
        if (shouldDismiss) {
            isVisible = false
            delay(200.milliseconds)
            onDismiss()
        }
    }

    // 退场（无论由谁触发）播完后才把组件移出组合，否则内容会一直留在组合里。
    LaunchedEffect(isVisible, visible) {
        if (!isVisible && !visible) {
            delay(200.milliseconds)
            exitDone = true
        }
    }

    FullscreenPopup(onDismiss = triggerDismiss) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val dimAlpha by animateFloatAsState(if (isVisible) 0.6f else 0f, label = "dim")
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = dimAlpha))
                    .bouncyClickable(triggerDismiss)
            )

            PredictiveBackHandler(
                backDispatcher = backDispatcher,
                onProgress = { backProgress = it },
                onDismiss = triggerDismiss
            )

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + scaleIn(initialScale = 0.9f),
                exit = fadeOut() + scaleOut(targetScale = 0.9f)
            ) {
                Surface(
                    modifier = modifier
                        .padding(horizontal = 24.dp)
                        .widthIn(max = 400.dp)
                        .graphicsLayer {
                            scaleX = 1f - (backProgress * 0.1f)
                            scaleY = 1f - (backProgress * 0.1f)
                            alpha = 1f - (backProgress * 0.3f)
                        },
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        fields.forEachIndexed { index, config ->
                            OutlinedTextField(
                                value = values[index],
                                onValueChange = { newValue ->
                                    values[index] = newValue
                                },
                                label = { Text(config.label) },
                                placeholder = { Text(config.placeholder) },
                                isError = errorMessages[index] != null,
                                supportingText = errorMessages[index]?.let {
                                    { Text(it, color = MaterialTheme.colorScheme.error) }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = config.keyboardType),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp)
                            )
                            if (index < fields.size - 1) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = triggerDismiss, shapes = ButtonDefaults.shapes()) {
                                Text(actualDismissText)
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = { onConfirm(values.toList()) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.87f)),
                                shapes = ButtonDefaults.shapes(),
                                enabled = isAllValid
                            ) {
                                Text(actualConfirmText)
                            }
                        }
                    }
                }
            }
        }
    }
}
