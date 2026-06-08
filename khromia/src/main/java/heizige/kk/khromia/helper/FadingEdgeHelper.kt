package heizige.kk.khromia.helper

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 增强版边缘淡化工具。
 *
 * @param top 顶部淡化高度
 * @param bottom 底部淡化高度
 * @param left 左侧淡化宽度
 * @param right 右侧淡化宽度
 * @param strength 模糊力度 (0.0 ~ 1.0)，1.0 为完全淡化到透明，值越小边缘越清晰
 */
fun Modifier.fadingEdge(
    top: Dp = 0.dp,
    bottom: Dp = 0.dp,
    left: Dp = 0.dp,
    right: Dp = 0.dp,
    strength: Float = 1f
): Modifier {
    val edgeAlpha = (1f - strength).coerceIn(0f, 1f)
    
    return this
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()

            // 顶部
            if (top > 0.dp) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = edgeAlpha), Color.Black),
                        startY = 0f,
                        endY = top.toPx()
                    ),
                    blendMode = BlendMode.DstIn
                )
            }

            // 底部
            if (bottom > 0.dp) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Black, Color.Black.copy(alpha = edgeAlpha)),
                        startY = size.height - bottom.toPx(),
                        endY = size.height
                    ),
                    blendMode = BlendMode.DstIn
                )
            }

            // 左侧
            if (left > 0.dp) {
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Black.copy(alpha = edgeAlpha), Color.Black),
                        startX = 0f,
                        endX = left.toPx()
                    ),
                    blendMode = BlendMode.DstIn
                )
            }

            // 右侧
            if (right > 0.dp) {
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Black, Color.Black.copy(alpha = edgeAlpha)),
                        startX = size.width - right.toPx(),
                        endX = size.width
                    ),
                    blendMode = BlendMode.DstIn
                )
            }
        }
}
