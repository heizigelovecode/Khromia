package heizige.kk.khromia.components

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import heizige.kk.khromia.helper.PredictiveBackHandler
import heizige.kk.khromia.layout.FullscreenPopup


private const val AlertDialogMotionDurationMillis = 260

/**
 * A Material 3-style alert dialog rendered through Khromia's toast popup host.
 *
 * It deliberately uses the same decor-view overlay as [GlobalToastHost], so the bottom-to-center
 * motion, fade and scale are controlled entirely by Compose. Keep this composable mounted and
 * drive it with [visible]; [onDismissRequest] runs after the exit transition completes.
 */
@Composable
fun AnimatedAlertDialog(
    visible: Boolean = true,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    properties: DialogProperties = DialogProperties(),
    enablePredictiveBack: Boolean = true
) {
    val visibilityState = remember { MutableTransitionState(false) }
    var hasBeenShown by remember { mutableStateOf(false) }
    var isDismissing by remember { mutableStateOf(false) }
    var backProgress by remember { mutableFloatStateOf(0f) }
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    fun requestDismiss() {
        if (!visibilityState.currentState && !visibilityState.targetState) return
        isDismissing = true
        visibilityState.targetState = false
    }

    LaunchedEffect(visible) {
        if (visible) {
            hasBeenShown = true
            backProgress = 0f
            visibilityState.targetState = true
        } else {
            if (hasBeenShown) isDismissing = true
            visibilityState.targetState = false
        }
    }

    LaunchedEffect(visibilityState.isIdle, visibilityState.currentState) {
        if (visibilityState.isIdle && !visibilityState.currentState && isDismissing) {
            isDismissing = false
            currentOnDismissRequest()
        }
    }

    if (!visible && !visibilityState.currentState && !visibilityState.targetState) return

    FullscreenPopup(
        onDismiss = if (properties.dismissOnBackPress) ::requestDismiss else null
    ) {
        val dimAlpha by animateFloatAsState(
            targetValue = if (visibilityState.targetState) 0.32f else 0f,
            animationSpec = tween(AlertDialogMotionDurationMillis),
            label = "AnimatedAlertDialogScrim"
        )
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = dimAlpha))
                    .then(
                        if (properties.dismissOnClickOutside) {
                            Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = ::requestDismiss
                            )
                        } else {
                            Modifier
                        }
                    )
            )

            if (enablePredictiveBack && properties.dismissOnBackPress) {
                PredictiveBackHandler(
                    backDispatcher = backDispatcher,
                    onProgress = { backProgress = it },
                    onDismiss = ::requestDismiss
                )
            }

            // The AnimatedVisibility child fills the screen, making its vertical offset originate
            // below the viewport instead of only below the dialog's own height.
            AnimatedVisibility(
                visibleState = visibilityState,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(AlertDialogMotionDurationMillis)
                ) + fadeIn(animationSpec = tween(AlertDialogMotionDurationMillis)) + scaleIn(
                    initialScale = 0.92f,
                    animationSpec = tween(AlertDialogMotionDurationMillis)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(AlertDialogMotionDurationMillis)
                ) + fadeOut(animationSpec = tween(AlertDialogMotionDurationMillis)) + scaleOut(
                    targetScale = 0.92f,
                    animationSpec = tween(AlertDialogMotionDurationMillis)
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AlertDialogSurface(
                        confirmButton = confirmButton,
                        dismissButton = dismissButton,
                        icon = icon,
                        title = title,
                        text = text,
                        modifier = modifier.graphicsLayer {
                            translationY = backProgress * size.height * 0.45f
                            scaleX = 1f - backProgress * 0.06f
                            scaleY = 1f - backProgress * 0.06f
                            alpha = 1f - backProgress * 0.3f
                        },
                        shape = shape,
                        containerColor = containerColor,
                        iconContentColor = iconContentColor,
                        titleContentColor = titleContentColor,
                        textContentColor = textContentColor,
                        tonalElevation = tonalElevation
                    )
                }
            }
        }
    }
}

@Composable
private fun AlertDialogSurface(
    confirmButton: @Composable () -> Unit,
    dismissButton: (@Composable () -> Unit)?,
    icon: (@Composable () -> Unit)?,
    title: (@Composable () -> Unit)?,
    text: (@Composable () -> Unit)?,
    modifier: Modifier,
    shape: Shape,
    containerColor: Color,
    iconContentColor: Color,
    titleContentColor: Color,
    textContentColor: Color,
    tonalElevation: Dp
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .sizeIn(minWidth = 280.dp, maxWidth = 560.dp),
        shape = shape,
        color = containerColor,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = tonalElevation
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            icon?.let { content ->
                CompositionLocalProvider(LocalContentColor provides iconContentColor) {
                    content()
                }
                if (title != null || text != null) {
                    androidx.compose.foundation.layout.Spacer(Modifier.height(16.dp))
                }
            }
            title?.let { content ->
                CompositionLocalProvider(LocalContentColor provides titleContentColor) {
                    content()
                }
                if (text != null) {
                    androidx.compose.foundation.layout.Spacer(Modifier.height(16.dp))
                }
            }
            text?.let { content ->
                CompositionLocalProvider(LocalContentColor provides textContentColor) {
                    content()
                }
            }
            androidx.compose.foundation.layout.Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                dismissButton?.invoke()
                confirmButton()
            }
        }
    }
}
