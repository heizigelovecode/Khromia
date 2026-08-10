package heizige.kk.khromia.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.material3.ToggleFloatingActionButtonScope
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val FabPressedCornerRadius = 8.dp
private val FabRestingCornerRadius = 16.dp

@Composable
private fun animatedFabCornerRadius(
    interactionSource: MutableInteractionSource,
    restingCornerRadius: Dp
): Dp {
    val isPressed by interactionSource.collectIsPressedAsState()
    return animateDpAsState(
        targetValue = if (isPressed) FabPressedCornerRadius else restingCornerRadius,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "FabCornerRadius"
    ).value
}

@Composable
fun AnimatedFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    restingCornerRadius: Dp = FabRestingCornerRadius,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val cornerRadius = animatedFabCornerRadius(interactionSource, restingCornerRadius)
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(cornerRadius),
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = elevation,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun AnimatedExtendedFloatingActionButton(
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    restingCornerRadius: Dp = FabRestingCornerRadius
) {
    val interactionSource = remember { MutableInteractionSource() }
    val cornerRadius = animatedFabCornerRadius(interactionSource, restingCornerRadius)
    ExtendedFloatingActionButton(
        text = text,
        icon = icon,
        onClick = onClick,
        modifier = modifier,
        expanded = expanded,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(cornerRadius),
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = elevation,
        interactionSource = interactionSource
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AnimatedToggleFloatingActionButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ToggleFloatingActionButtonScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val defaultCornerRadius = ToggleFloatingActionButtonDefaults.containerCornerRadius()
    val restingCornerRadius = defaultCornerRadius(if (checked) 1f else 0f)
    val cornerRadius by animateDpAsState(
        targetValue = if (isPressed) FabPressedCornerRadius else restingCornerRadius,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "ToggleFabCornerRadius"
    )

    ToggleFloatingActionButton(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier.observePressInteractions(interactionSource),
        containerCornerRadius = { cornerRadius },
        content = content
    )
}

private fun Modifier.observePressInteractions(
    interactionSource: MutableInteractionSource
): Modifier = pointerInput(interactionSource) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val press = PressInteraction.Press(down.position)
        interactionSource.tryEmit(press)
        try {
            do {
                val event = awaitPointerEvent()
            } while (event.changes.any { it.pressed })
            interactionSource.tryEmit(PressInteraction.Release(press))
        } catch (throwable: Throwable) {
            interactionSource.tryEmit(PressInteraction.Cancel(press))
            throw throwable
        }
    }
}
