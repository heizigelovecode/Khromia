package heizige.kk.khromia.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FancySlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true,
    onValueChangeFinished: (() -> Unit)? = null,
    thumbSize: Dp = 24.dp,
    trackHeight: Dp = 36.dp,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDragged by interactionSource.collectIsDraggedAsState()
    val animatedValue by animateFloatAsState(
        targetValue = value,
        animationSpec = if (isDragged) snap() else tween(100),
    )

    val rangeLength = valueRange.endInclusive - valueRange.start
    val fraction = if (rangeLength == 0f) {
        0f
    } else {
        ((animatedValue - valueRange.start) / rangeLength).coerceIn(0f, 1f)
    }
    val targetFraction = if (rangeLength == 0f) {
        0f
    } else {
        ((value - valueRange.start) / rangeLength).coerceIn(0f, 1f)
    }

    val colors = SliderDefaults.colors(
        thumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
        activeTrackColor = MaterialTheme.colorScheme.primaryContainer,
        inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        activeTickColor = MaterialTheme.colorScheme.onPrimaryContainer,
        inactiveTickColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
    )

    Slider(
        value = animatedValue,
        onValueChange = onValueChange,
        modifier = modifier,
        valueRange = valueRange,
        steps = steps,
        enabled = enabled,
        onValueChangeFinished = onValueChangeFinished,
        colors = colors,
        interactionSource = interactionSource,
        thumb = {
            SunThumb(
                fraction = if (steps > 0) targetFraction else fraction,
                animateRotation = steps > 0,
                size = thumbSize,
                containerSize = trackHeight,
                color = if (enabled) colors.thumbColor else colors.disabledThumbColor,
            )
        },
        track = { sliderState ->
            FancySliderTrack(
                sliderState = sliderState,
                steps = steps,
                colors = colors,
                trackHeight = trackHeight,
            )
        },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SunThumb(
    fraction: Float,
    animateRotation: Boolean,
    size: Dp,
    containerSize: Dp,
    color: Color,
) {
    val sunnyShape = MaterialShapes.Sunny.toShape()
    val rotation by animateFloatAsState(
        targetValue = fraction * 1080f,
        animationSpec = if (animateRotation) tween(180) else snap(),
    )

    Box(
        modifier = Modifier.size(containerSize),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(size.coerceAtMost(containerSize))
                .graphicsLayer { rotationZ = rotation }
                .background(color = color, shape = sunnyShape),
        )
    }
}

@Composable
private fun FancySliderTrack(
    sliderState: SliderState,
    steps: Int,
    colors: SliderColors,
    trackHeight: Dp,
) {
    val activeColor = colors.activeTrackColor
    val inactiveColor = colors.inactiveTrackColor
    val activeTickColor = colors.activeTickColor
    val inactiveTickColor = colors.inactiveTickColor

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(trackHeight)
    ) {
        val trackWidth = size.width
        val capRadius = size.height / 2f
        val trackStart = -capRadius
        val fullTrackWidth = trackWidth + size.height
        val thumbCenter = trackWidth * sliderState.coercedValueAsFraction
        val activeWidth = thumbCenter + size.height

        drawRoundRect(
            color = inactiveColor,
            topLeft = Offset(trackStart, 0f),
            size = Size(fullTrackWidth, size.height),
            cornerRadius = CornerRadius(capRadius, capRadius),
        )

        drawRoundRect(
            color = activeColor,
            topLeft = Offset(trackStart, 0f),
            size = Size(activeWidth, size.height),
            cornerRadius = CornerRadius(capRadius, capRadius),
        )

        if (steps > 0) {
            val tickCount = steps + 1
            for (i in 1 until tickCount) {
                val f = i.toFloat() / tickCount
                val isActive = f <= sliderState.coercedValueAsFraction
                val x = trackWidth * f
                drawCircle(
                    color = if (isActive) activeTickColor else inactiveTickColor,
                    radius = 3.dp.toPx(),
                    center = Offset(x, center.y),
                )
            }
        }
    }
}
