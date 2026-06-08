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

/**
 * 编辑框配置类
 * @param label 标签文本
 * @param initialValue 初始值
 * @param placeholder 占位符
 * @param keyboardType 键盘类型 (例如 KeyboardType.Number)
 * @param range 数值范围 (仅在数字输入时有效)
 * @param maxLength 最大长度限制
 * @param onValidate 自定义校验逻辑，返回错误信息字符串，为 null 表示通过
 */
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
 * 通用编辑弹窗组件
 * 支持多个编辑框、输入类型限制以及范围校验
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
    if (!visible) return

    val actualConfirmText = confirmText ?: stringResource(R.string.edit_dialog_confirm)
    val actualDismissText = dismissText ?: stringResource(R.string.edit_dialog_cancel)

    val errInvalidNumber = stringResource(R.string.edit_dialog_error_invalid_number)
    val errRangeTemplate = stringResource(R.string.edit_dialog_error_range)
    val errMaxLengthTemplate = stringResource(R.string.edit_dialog_error_max_length)

    // 使用 state 列表保存每个输入框的值
    val values = remember(fields) {
        mutableStateListOf<String>().apply {
            addAll(fields.map { it.initialValue })
        }
    }

    // 自动同步初始值（当 fields 发生变化时）
    LaunchedEffect(fields) {
        values.clear()
        values.addAll(fields.map { it.initialValue })
    }

    // 计算每个输入框的错误状态
    val errorMessages = fields.mapIndexed { index, config ->
        val value = values[index]
        
        // 1. 基础数字校验
        if (config.keyboardType == KeyboardType.Number || config.keyboardType == KeyboardType.Decimal) {
            val num = value.toDoubleOrNull()
            if (value.isNotEmpty() && num == null) {
                return@mapIndexed errInvalidNumber
            }
            // 2. 范围校验
            if (num != null && config.range != null) {
                if (num !in config.range) {
                    return@mapIndexed errRangeTemplate.format(config.range.start, config.range.endInclusive)
                }
            }
        }

        // 3. 长度校验
        if (config.maxLength != null && value.length > config.maxLength) {
            return@mapIndexed errMaxLengthTemplate.format(config.maxLength)
        }

        // 4. 自定义校验
        config.onValidate?.invoke(value)
    }

    val isAllValid = errorMessages.all { it == null }

    var isVisible by remember { mutableStateOf(false) }
    var shouldDismiss by remember { mutableStateOf(false) }
    var backProgress by remember { mutableFloatStateOf(0f) }
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val triggerDismiss = { shouldDismiss = true }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    LaunchedEffect(shouldDismiss) {
        if (shouldDismiss) {
            isVisible = false
            delay(200)
            onDismiss()
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
                    .clickable(
                        onClick = triggerDismiss,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
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
                                shape = RoundedCornerShape(20.dp)
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
