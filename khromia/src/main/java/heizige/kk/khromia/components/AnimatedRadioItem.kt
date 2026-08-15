package heizige.kk.khromia.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import heizige.kk.khromia.motion.effectsSpec
import heizige.kk.khromia.motion.expandFadeIn
import heizige.kk.khromia.motion.shrinkFadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 带弹簧动画的单选列表项组件
 *
 * @param text 显示的文本
 * @param isSelected 是否被选中
 * @param onClick 点击回调
 * @param modifier 修饰符
 * @param subtitle 未选中时显示的说明；选中后收起，与 [ExpandableOptionItem] 一致
 * @param checkIcon 选中时显示的图标（默认为 check 图标）
 * @param selectedBackground 选中时的背景色
 * @param unselectedBackground 未选中时的背景色
 * @param textStyle 文本样式
 * @param textColor 文本颜色
 * @param checkIconTint 选中图标的颜色
 * @param verticalPadding 垂直内边距
 * @param horizontalPadding 水平内边距
 * @param cornerRadius 圆角半径
 */
@Composable
fun AnimatedRadioItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    checkIcon: Painter? = null,
    selectedBackground: Color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.54f),
    unselectedBackground: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.26f),
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    checkIconTint: Color = MaterialTheme.colorScheme.primary,
    verticalPadding: Dp = 0.dp,
    horizontalPadding: Dp = 16.dp,
    cornerRadius: Dp = 4.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val themeBg by animateColorAsState(
        targetValue = if (isSelected) selectedBackground else unselectedBackground,
        animationSpec = effectsSpec(),
        label = "themeBg"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = verticalPadding)
            .clip(RoundedCornerShape(cornerRadius))
            .background(themeBg)
            .pressBounce(interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = horizontalPadding, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = text,
                    style = textStyle,
                    color = textColor
                )
                if (subtitle != null) {
                    AnimatedVisibility(
                        visible = !isSelected,
                        enter = expandFadeIn(),
                        exit = shrinkFadeOut()
                    ) {
                        Text(
                            text = subtitle,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.54f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
            AnimatedSelectionCheck(
                selected = isSelected,
                tint = checkIconTint,
                icon = checkIcon
            )
        }
    }
}
