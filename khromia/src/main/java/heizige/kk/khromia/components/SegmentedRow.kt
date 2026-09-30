package heizige.kk.khromia.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import heizige.kk.khromia.motion.effectsSpec

/**
 * 分段选择控件：把若干选项并排在一行，替代 Material3 的
 * `SingleChoiceSegmentedButtonRow` / `MultiChoiceSegmentedButtonRow`。
 *
 * 与 M3 的差别：整行共享一个圆角底板，选中项在其上以高亮块呈现（而不是每段各自描边），
 * 交互沿用 Khromia 的按压回弹 + 颜色过渡，视觉上与 [OptionItem] / [AnimatedRadioItem] 同源。
 *
 * 选中状态由调用方持有（[SegmentedItem.selected]），本组件只负责呈现与回调。
 */

/** 分段选择项。 */
@Immutable
data class SegmentedItem(
    val label: String,
    val selected: Boolean = false,
    val onClick: () -> Unit = {},
    val leadingIcon: ImageVector? = null,
    val enabled: Boolean = true,
)

/**
 * 单选分段控件：任一时刻至多一项选中，语义上为一组单选按钮。
 *
 * ```
 * SingleChoiceSegmentedRow(
 *     items = listOf(
 *         SegmentedItem("Basic", selected = true) { ... },
 *         SegmentedItem("Advanced") { ... },
 *     ),
 * )
 * ```
 */
@Composable
fun SingleChoiceSegmentedRow(
    items: List<SegmentedItem>,
    modifier: Modifier = Modifier,
    shape: Shape = SegmentedRowDefaults.shape,
    selectedContainerColor: Color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.54f),
    unselectedContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.26f),
    selectedContentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    unselectedContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    contentPadding: Dp = SegmentedRowDefaults.ContentPadding,
) {
    SegmentedRowBase(
        items = items,
        modifier = modifier,
        shape = shape,
        isMultiChoice = false,
        selectedContainerColor = selectedContainerColor,
        unselectedContainerColor = unselectedContainerColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        contentPadding = contentPadding,
    )
}

/** 多选分段控件：各项可独立勾选与取消，语义上为一组复选按钮。 */
@Composable
fun MultiChoiceSegmentedRow(
    items: List<SegmentedItem>,
    modifier: Modifier = Modifier,
    shape: Shape = SegmentedRowDefaults.shape,
    selectedContainerColor: Color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.54f),
    unselectedContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.26f),
    selectedContentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    unselectedContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    contentPadding: Dp = SegmentedRowDefaults.ContentPadding,
) {
    SegmentedRowBase(
        items = items,
        modifier = modifier,
        shape = shape,
        isMultiChoice = true,
        selectedContainerColor = selectedContainerColor,
        unselectedContainerColor = unselectedContainerColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        contentPadding = contentPadding,
    )
}

@Composable
private fun SegmentedRowBase(
    items: List<SegmentedItem>,
    modifier: Modifier,
    shape: Shape,
    isMultiChoice: Boolean,
    selectedContainerColor: Color,
    unselectedContainerColor: Color,
    selectedContentColor: Color,
    unselectedContentColor: Color,
    contentPadding: Dp,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup()
            .clip(shape)
            .background(unselectedContainerColor)
            .padding(SegmentedRowDefaults.ItemSpacing),
        horizontalArrangement = Arrangement.spacedBy(SegmentedRowDefaults.ItemSpacing),
    ) {
        items.forEach { item ->
            SegmentedItemView(
                item = item,
                isMultiChoice = isMultiChoice,
                selectedContainerColor = selectedContainerColor,
                selectedContentColor = selectedContentColor,
                unselectedContentColor = unselectedContentColor,
                contentPadding = contentPadding,
            )
        }
    }
}

@Composable
private fun RowScope.SegmentedItemView(
    item: SegmentedItem,
    isMultiChoice: Boolean,
    selectedContainerColor: Color,
    selectedContentColor: Color,
    unselectedContentColor: Color,
    contentPadding: Dp,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val containerColor by animateColorAsState(
        targetValue = if (item.selected) selectedContainerColor else Color.Transparent,
        animationSpec = effectsSpec(),
        label = "segmentedContainer"
    )
    val contentColor by animateColorAsState(
        targetValue = if (item.selected) selectedContentColor else unselectedContentColor,
        animationSpec = effectsSpec(),
        label = "segmentedContent"
    )
    val leadingPainter = item.leadingIcon?.let { rememberVectorPainter(it) }

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .pressBounce(interactionSource)
                .weight(1f)
                .clip(RoundedCornerShape(SegmentedRowDefaults.ItemCornerRadius))
                .background(containerColor)
                // 用 selectable/toggleable 而非 clickable：它们会写入 selected/role 语义，
                // 读屏软件才能播报「当前选中第几项」。
                .then(
                    if (isMultiChoice) {
                        Modifier.toggleable(
                            value = item.selected,
                            enabled = item.enabled,
                            role = Role.Checkbox,
                            interactionSource = interactionSource,
                            indication = null,
                            onValueChange = { item.onClick() },
                        )
                    } else {
                        Modifier.selectable(
                            selected = item.selected,
                            enabled = item.enabled,
                            role = Role.RadioButton,
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { item.onClick() },
                        )
                    }
                )
                .padding(
                    horizontal = contentPadding,
                    vertical = SegmentedRowDefaults.ItemVerticalPadding
                )
        ) {
            if (leadingPainter != null) {
                Icon(
                    painter = leadingPainter,
                    contentDescription = null,
                    modifier = Modifier.size(SegmentedRowDefaults.IconSize)
                )
                Spacer(Modifier.width(SegmentedRowDefaults.IconTextSpacing))
            }
            ProvideTextStyle(value = MaterialTheme.typography.labelLarge.copy(
                lineHeight = MaterialTheme.typography.labelLarge.lineHeight,
            )) {
                // 不限行数：窄屏 + 大字号下「对话模型」这类长标签会被 maxLines=1 截断。
                Text(text = item.label, textAlign = TextAlign.Center)
            }
        }
    }
}

/** 分段选择控件的默认尺寸与形状。 */
object SegmentedRowDefaults {
    /**
     * 整行底板圆角。取 20dp 与 [OptionItem] / [ButtonOption] 的默认圆角一致，
     * 避免同一屏里出现两种列表面圆角。
     */
    val shape: Shape @Composable get() = RoundedCornerShape(20.dp)

    val ItemCornerRadius = 14.dp
    val ContentPadding = 16.dp
    val ItemVerticalPadding = 10.dp
    val ItemSpacing = 4.dp
    val IconSize = 18.dp
    val IconTextSpacing = 6.dp
}
