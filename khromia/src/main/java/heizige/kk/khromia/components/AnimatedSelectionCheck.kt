package heizige.kk.khromia.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import heizige.kk.khromia.motion.spatialSpec

@Composable
internal fun AnimatedSelectionCheck(
    selected: Boolean,
    tint: Color,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    size: Dp = 20.dp
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = spatialSpec(),
        label = "selectionCheckScale"
    )
    val rotation by animateFloatAsState(
        targetValue = if (selected) 0f else 180f,
        animationSpec = spatialSpec(),
        label = "selectionCheckRotation"
    )
    val actualIcon = icon ?: rememberVectorPainter(Icons.Rounded.Check)

    if (scale > 0f) {
        Icon(
            painter = actualIcon,
            contentDescription = null,
            tint = tint,
            modifier = modifier
                .size(size)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    rotationZ = rotation
                }
        )
    }
}
