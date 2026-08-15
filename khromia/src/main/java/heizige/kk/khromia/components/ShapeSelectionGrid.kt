package heizige.kk.khromia.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import heizige.kk.khromia.motion.effectsSpec

@Immutable
data class ShapeSelectionOption(
    val key: String,
    val shape: Shape
)

/**
 * A compact shape picker with animated selection and press feedback.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShapeSelectionGrid(
    options: List<ShapeSelectionOption>,
    selectedKey: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 4,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    itemSize: Dp = 56.dp,
    checkSize: Dp = 24.dp,
    selectedColor: Color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.54f),
    unselectedColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.54f),
    checkColor: Color = MaterialTheme.colorScheme.primary,
    previewContent: (@Composable (ShapeSelectionOption) -> Unit)? = null
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        maxItemsInEachRow = columns
    ) {
        options.forEach { option ->
            val selected = selectedKey == option.key
            val interactionSource = remember(option.key) { MutableInteractionSource() }
            val pressed by interactionSource.collectIsPressedAsState()
            val selectionScale = remember { Animatable(1f) }
            var hasAppeared by remember { mutableStateOf(false) }
            LaunchedEffect(selected) {
                if (selected && hasAppeared) {
                    selectionScale.snapTo(0.82f)
                    selectionScale.animateTo(
                        1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                }
                hasAppeared = true
            }
            val itemScale by animateFloatAsState(
                targetValue = if (pressed) 0.88f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "shapeItemScale"
            )
            val shapeColor by animateColorAsState(
                targetValue = if (selected) selectedColor else unselectedColor,
                animationSpec = effectsSpec(),
                label = "shapeItemColor"
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onSelected(option.key) }
                    )
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(itemSize)
                        .graphicsLayer {
                            scaleX = itemScale * selectionScale.value
                            scaleY = itemScale * selectionScale.value
                        }
                        .clip(option.shape)
                        .background(shapeColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (previewContent != null) {
                        previewContent(option)
                    }
                    AnimatedSelectionCheck(
                        selected = selected,
                        tint = checkColor,
                        size = checkSize
                    )
                }
            }
        }
    }
}
