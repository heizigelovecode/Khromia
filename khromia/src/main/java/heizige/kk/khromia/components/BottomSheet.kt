package heizige.kk.khromia.components

import android.os.Build
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import heizige.kk.khromia.data.BottomSheetData
import heizige.kk.khromia.helper.PredictiveBackHandler
import heizige.kk.khromia.layout.BottomSheetLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrimaryBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    enablePredictiveBack: Boolean = true,
    content: @Composable () -> Unit
) {
    if (!visible) return

    val view = LocalView.current
    val parentComposition = rememberCompositionContext()
    val currentContent by rememberUpdatedState(content)
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val popupId = rememberSaveable { UUID.randomUUID() }
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    var isContentVisible by remember { mutableStateOf(false) }
    var shouldDismiss by remember { mutableStateOf(false) }

    val bottomSheetLayout = remember {
        BottomSheetLayout(
            onDismiss = currentOnDismiss,
            composeView = view,
            popupId = popupId,
            backDispatcher = backDispatcher,
            enablePredictiveBack = enablePredictiveBack
        ).apply {
            setContent(parentComposition) {
                BasicBottomSheetContent(
                    isVisible = isContentVisible,
                    onDismiss = { shouldDismiss = true },
                    modifier = modifier,
                    enablePredictiveBack = enablePredictiveBack,
                    backDispatcher = backDispatcher,
                    content = currentContent
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(300)
        isContentVisible = true
    }


    LaunchedEffect(shouldDismiss) {
        if (shouldDismiss) {
            isContentVisible = false
            delay(300)
            currentOnDismiss()
        }
    }

    DisposableEffect(bottomSheetLayout) {
        bottomSheetLayout.show()
        onDispose {
            bottomSheetLayout.disposeComposition()
            bottomSheetLayout.dismiss()
        }
    }

    SideEffect {
        bottomSheetLayout.updateContent {
            BasicBottomSheetContent(
                isVisible = isContentVisible,
                onDismiss = { shouldDismiss = true },
                modifier = modifier,
                enablePredictiveBack = enablePredictiveBack,
                backDispatcher = backDispatcher,
                content = currentContent
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BasicBottomSheetContent(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier,
    enablePredictiveBack: Boolean,
    backDispatcher: OnBackPressedDispatcher?,
    content: @Composable () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0f) }
    val offsetY = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetData = BottomSheetData()

    if (enablePredictiveBack && Build.VERSION.SDK_INT >= 33) {
        PredictiveBackHandler(
            backDispatcher = backDispatcher,
            onProgress = { progress = it },
            onDismiss = onDismiss
        )
    } else {
        BackHandler(onBack = onDismiss)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                onClick = onDismiss,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically (tween (bottomSheetData.duration)){ it },
            exit = slideOutVertically (tween (bottomSheetData.duration)){ it } + fadeOut(tween (bottomSheetData.duration))
        ) {
            val draggableState = rememberDraggableState { delta ->
                coroutineScope.launch {
                    val deltaY = if (offsetY.value + delta >= 0f) offsetY.value + delta else 0f
                    offsetY.snapTo(deltaY)
                }
            }

            Surface(
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 600.dp)
                    .offset { IntOffset(0, offsetY.value.toInt()) }
                    .graphicsLayer {
                        val backProgress = progress
                        translationY = size.height * backProgress * bottomSheetData.translationY
                        scaleX = 1f - (backProgress * bottomSheetData.scale)
                        scaleY = 1f - (backProgress * bottomSheetData.scale)
                        alpha = 1f - (backProgress * bottomSheetData.alpha)
                    }
                    .draggable(
                        state = draggableState,
                        orientation = Orientation.Vertical,
                        onDragStopped = { velocity ->
                            val currentOffset = offsetY.value
                            val threshold = 300f

                            when {
                                currentOffset < -threshold -> {
                                    coroutineScope.launch {
                                        offsetY.animateTo(0f)
                                    }
                                }
                                currentOffset > threshold -> {
                                    onDismiss()
                                }
                                else -> {
                                    coroutineScope.launch {
                                        if (abs(velocity) > 500f) {
                                            if (velocity < 0) {
                                                offsetY.animateTo(0f)
                                            } else {
                                                onDismiss()
                                            }
                                        } else {
                                            offsetY.animateTo(0f)
                                        }
                                    }
                                }
                            }
                        }
                    )
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column {
                    content()
                }
            }
        }
    }

}