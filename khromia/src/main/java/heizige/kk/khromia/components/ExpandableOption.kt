package heizige.kk.khromia.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import heizige.kk.khromia.text.OptionText

@Composable
fun ExpandableOptionItem(
    modifier: Modifier = Modifier,
    painter: Painter,
    title: String,
    subtitle: String? = null,
    initiallyExpanded: Boolean = false,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.26f),
    contentColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.26f),
    shape: Shape = RoundedCornerShape(20.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }

    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) -180f else 0f,
        animationSpec = spring(
            dampingRatio = if (isExpanded) Spring.DampingRatioMediumBouncy else Spring.DampingRatioNoBouncy,
            stiffness = if (isExpanded) Spring.StiffnessLow else Spring.StiffnessMedium
        ),
        label = "rotation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .clickable { isExpanded = !isExpanded }
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.54f), CircleShape)
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.87f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
                OptionText(text = title)
                if (subtitle != null) {
                    // 副标题动画：进入时弹，退出时快
                    AnimatedVisibility(
                        visible = !isExpanded,
                        enter = expandVertically(
                            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
                        ) + fadeIn(
                            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
                        ),
                        exit = shrinkVertically(
                            animationSpec = spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium)
                        ) + fadeOut(
                            animationSpec = spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium)
                        )
                    ) {
                        Text(
                            text = subtitle,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.54f),
                            maxLines = 1,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Rounded.KeyboardArrowUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.54f),
                modifier = Modifier.graphicsLayer(rotationZ = rotationAngle)
            )
        }

        if (isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp, start = 12.dp, end = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(contentColor)
            ) {
                content()
            }
        }
    }
}