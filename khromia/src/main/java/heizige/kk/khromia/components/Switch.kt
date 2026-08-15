package heizige.kk.khromia.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Switch as MaterialSwitch
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import heizige.kk.khromia.motion.effectsSpec
import heizige.kk.khromia.motion.spatialSpec

const val DefaultOptionSwitchScale = 0.875f

val LocalOptionSwitchScale = compositionLocalOf { DefaultOptionSwitchScale }
val LocalOptionSwitchThumbShape = compositionLocalOf<Shape?> { null }
val LocalOptionSwitchThumbShapeCheckedOnly = compositionLocalOf { false }
val LocalOptionSwitchExpressiveEnabled = compositionLocalOf { true }
val LocalOptionSwitchUncheckedThumbScale = compositionLocalOf { 2f / 3f }

private val SwitchTrackShape = RoundedCornerShape(50)

/** Khromia switch with an animated, configurable thumb shape (Sunny by default). */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OptionSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    thumbShape: Shape? = null,
) {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = LocalOptionSwitchScale.current
    val actualThumbContent: (@Composable () -> Unit)? = thumbContent ?: if (checked) {
        {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize)
            )
        }
    } else {
        null
    }

    if (!LocalOptionSwitchExpressiveEnabled.current) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            MaterialSwitch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        val width = (placeable.width * scale).toInt()
                        val height = (placeable.height * scale).toInt()
                        layout(width, height) {
                            placeable.placeRelative(
                                (width - placeable.width) / 2,
                                (height - placeable.height) / 2
                            )
                        }
                    },
                thumbContent = actualThumbContent,
                enabled = enabled,
                colors = colors,
                interactionSource = interactionSource
            )
        }
        return
    }

    val uncheckedThumbScale = LocalOptionSwitchUncheckedThumbScale.current.coerceIn(0.5f, 1.5f)
    val targetThumbSize = if (checked) 24.dp else 24.dp * uncheckedThumbScale
    val thumbSize by animateDpAsState(
        targetValue = targetThumbSize,
        animationSpec = spatialSpec(),
        label = "switchThumbSize"
    )
    val thumbOffset by animateDpAsState(
        // Keep the compact unchecked thumb aligned with the track's 8dp inset.
        targetValue = if (checked) 48.dp - targetThumbSize else 8.dp,
        animationSpec = spatialSpec(),
        label = "switchThumbOffset"
    )
    val trackColor by animateColorAsState(
        targetValue = when {
            !enabled && checked -> colors.disabledCheckedTrackColor
            !enabled -> colors.disabledUncheckedTrackColor
            checked -> colors.checkedTrackColor
            else -> colors.uncheckedTrackColor
        },
        animationSpec = effectsSpec(),
        label = "switchTrackColor"
    )
    val thumbColor by animateColorAsState(
        targetValue = when {
            !enabled && checked -> colors.disabledCheckedThumbColor
            !enabled -> colors.disabledUncheckedThumbColor
            checked -> colors.checkedThumbColor
            else -> colors.uncheckedThumbColor
        },
        animationSpec = effectsSpec(),
        label = "switchThumbColor"
    )
    val thumbToggleScale by animateFloatAsState(
        targetValue = if (checked) 1f else 0.92f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "switchThumbToggleScale"
    )
    val thumbRotation by animateFloatAsState(
        targetValue = if (checked) 0f else -8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "switchThumbRotation"
    )
    val borderColor = when {
        !enabled && checked -> colors.disabledCheckedBorderColor
        !enabled -> colors.disabledUncheckedBorderColor
        checked -> colors.checkedBorderColor
        else -> colors.uncheckedBorderColor
    }
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = spatialSpec(),
        label = "switchPressScale"
    )
    val actualEnabled = enabled && onCheckedChange != null
    val defaultThumbShape = MaterialShapes.Sunny.toShape()
    val configuredThumbShape = thumbShape ?: LocalOptionSwitchThumbShape.current
    val checkedOnly = thumbShape == null && LocalOptionSwitchThumbShapeCheckedOnly.current
    val actualThumbShape = if (!checked && checkedOnly) {
        CircleShape
    } else {
        configuredThumbShape ?: defaultThumbShape
    }

    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Box(
            modifier = modifier
                .size(width = 52.dp, height = 32.dp)
                .graphicsLayer {
                    scaleX = pressScale * scale
                    scaleY = pressScale * scale
                }
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    val width = (placeable.width * scale).toInt()
                    val height = (placeable.height * scale).toInt()
                    layout(width, height) {
                        placeable.placeRelative(
                            (width - placeable.width) / 2,
                            (height - placeable.height) / 2
                        )
                    }
                }
                .toggleable(
                    value = checked,
                    enabled = actualEnabled,
                    role = Role.Switch,
                    interactionSource = interactionSource,
                    indication = null,
                    onValueChange = { onCheckedChange?.invoke(it) }
                )
                .clip(SwitchTrackShape)
                .background(trackColor)
                // Material's unchecked switch uses a visibly wider outline.
                .border(2.dp, borderColor, SwitchTrackShape),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(thumbSize)
                    .graphicsLayer {
                        scaleX = thumbToggleScale
                        scaleY = thumbToggleScale
                        rotationZ = thumbRotation
                    }
                    .clip(actualThumbShape)
                    .background(thumbColor),
                contentAlignment = Alignment.Center
            ) {
                if (actualThumbContent != null) {
                    androidx.compose.runtime.CompositionLocalProvider(
                        androidx.compose.material3.LocalContentColor provides
                            if (enabled) colors.checkedIconColor else colors.disabledCheckedIconColor
                    ) {
                        if (thumbContent == null) {
                            AnimatedVisibility(
                                visible = checked,
                                enter = fadeIn() + scaleIn(initialScale = 0.55f),
                                exit = fadeOut() + scaleOut(targetScale = 0.55f)
                            ) {
                                actualThumbContent()
                            }
                        } else {
                            actualThumbContent()
                        }
                    }
                }
            }
        }
    }
}
