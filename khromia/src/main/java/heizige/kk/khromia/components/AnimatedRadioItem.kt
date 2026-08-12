package heizige.kk.khromia.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 带弹簧动画的单选列表项组件
 *
 * @param text 显示的文本
 * @param isSelected 是否被选中
 * @param onClick 点击回调
 * @param modifier 修饰符
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
    val themeBg by animateColorAsState(
        targetValue = if (isSelected) selectedBackground else unselectedBackground,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "themeBg"
    )

    val checkScale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "checkScale"
    )
    val checkRotation by animateFloatAsState(
        targetValue = if (isSelected) 0f else 180f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "checkRotation"
    )

    val defaultCheckIcon = rememberVectorPainter(Icons.Default.Check)
    val actualIcon = checkIcon ?: defaultCheckIcon

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = verticalPadding)
            .clip(RoundedCornerShape(cornerRadius))
            .background(themeBg)
            .clickable(onClick = onClick)
            .padding(horizontal = horizontalPadding, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = textStyle,
                color = textColor
            )
            if (checkScale > 0f) {
                Icon(
                    painter = actualIcon,
                    contentDescription = null,
                    tint = checkIconTint,
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer {
                            scaleX = checkScale
                            scaleY = checkScale
                            rotationZ = checkRotation
                        }
                )
            }
        }
    }
}
