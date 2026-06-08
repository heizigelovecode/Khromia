package heizige.kk.khromia.components

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.res.stringResource
import heizige.kk.khromia.R
import heizige.kk.khromia.data.BottomSheetData
import heizige.kk.khromia.helper.PredictiveBackHandler
import heizige.kk.khromia.layout.BottomSheetLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.abs


val LocalBottomSheetDismiss = staticCompositionLocalOf<() -> Unit> {
    { }
}

@Composable
fun PrimaryBottomSheet(
    modifier: Modifier = Modifier,
    visible: Boolean,
    title: String,
    painter: Painter,
    dismissText: String? = null,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {

    val actualDismissText = dismissText ?: stringResource(R.string.bottom_sheet_dismiss)

    BasicBottomSheet(
        visible = visible,
        onDismiss = onDismiss,
        modifier = modifier,
        enablePredictiveBack = true,
        dragHandle = {
            Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.54f)).padding(vertical = 24.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement =Arrangement.Center){
                Box(modifier = Modifier.height(4.dp).width(64.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.87f)))
            }
        },
        bottomBar = {
            Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.26f)).padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement =Arrangement.Center){
                Icon(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.54f), CircleShape)
                        .padding(8.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.87f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(text = title, style = MaterialTheme.typography.labelLarge)

                Spacer(modifier = Modifier.weight(1f))

                val dismiss = LocalBottomSheetDismiss.current
                Button(onClick = { dismiss() },  shapes = ButtonDefaults.shapes()) {
                    Text(actualDismissText)
                }
            }
        },
        content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    )

}

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun BasicBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    enablePredictiveBack: Boolean = true,
    dragHandle: @Composable () -> Unit,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    if (!visible) return

    val view = LocalView.current
    val parentComposition = rememberCompositionContext()
    val currentDragHandle by rememberUpdatedState(dragHandle)
    val currentContent by rememberUpdatedState(content)
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val popupId = rememberSaveable { UUID.randomUUID() }
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    var isContentVisible by remember { mutableStateOf(false) }
    var shouldDismiss by remember { mutableStateOf(false) }
    val triggerDismiss = { shouldDismiss = true }

    val bottomSheetData = remember { BottomSheetData() }

    // 大屏检测逻辑
    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val isWideScreen = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    // 高度限制：最大占屏幕 0.9
    val maxHeight = screenHeight * 0.9f
    @Composable
    fun SheetContent() {
        val backgroundAlpha by animateFloatAsState(
            targetValue = if (isContentVisible) 0.6f else 0f,
            animationSpec = tween(bottomSheetData.duration),
            label = "backgroundAlpha"
        )

        var progress by remember { mutableFloatStateOf(0f) }
        val offsetY = remember { Animatable(0f) }
        val coroutineScope = rememberCoroutineScope()
        var sheetHeight by remember { mutableFloatStateOf(0f) }

        if (enablePredictiveBack && Build.VERSION.SDK_INT >= 33) {
            PredictiveBackHandler(
                backDispatcher = backDispatcher,
                onProgress = { progress = it },
                onDismiss = triggerDismiss
            )
        } else {
            BackHandler(onBack = triggerDismiss)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = backgroundAlpha))
                .clickable(
                    onClick = triggerDismiss,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                visible = isContentVisible,
                enter = slideInVertically(tween(bottomSheetData.duration)) { it },
                exit = slideOutVertically(tween(bottomSheetData.duration)) { it } + fadeOut(
                    tween(
                        bottomSheetData.duration
                    )
                )
            ) {
                val draggableState = rememberDraggableState { delta ->
                    coroutineScope.launch {
                        val targetOffset = offsetY.value + delta
                        offsetY.snapTo(if (targetOffset < 0f) targetOffset * 0.3f else targetOffset)
                    }
                }

                Surface(
                    modifier = modifier
                        .fillMaxWidth(if (isWideScreen) 0.8f else 1f)
                        .heightIn(min = 100.dp, max = maxHeight)
                        .onGloballyPositioned { sheetHeight = it.size.height.toFloat() }
                        .offset { IntOffset(0, offsetY.value.coerceAtLeast(0f).toInt()) }
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
                                val threshold = sheetHeight * 0.4f
                                when {
                                    currentOffset > threshold || (velocity > 800f && currentOffset > 0) -> triggerDismiss()
                                    else -> {
                                        coroutineScope.launch {
                                            offsetY.animateTo(0f)
                                        }
                                    }
                                }
                            }
                        )
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shadowElevation = 8.dp
                ) {
                    Column {
                        currentDragHandle()
                        Column(modifier = Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState())) {
                            currentContent()
                        }
                        bottomBar()
                    }
                }
            }
        }
    }

    val bottomSheetLayout = remember {
        BottomSheetLayout(
            onDismiss = triggerDismiss,
            composeView = view,
            popupId = popupId,
            backDispatcher = backDispatcher,
            enablePredictiveBack = enablePredictiveBack
        ).apply {
            setContent(parentComposition) {
                CompositionLocalProvider(LocalBottomSheetDismiss provides triggerDismiss) {
                    SheetContent()
                }
            }
        }
    }

    // 动画控制：进入
    LaunchedEffect(Unit) {
        delay(bottomSheetData.duration.toLong())
        isContentVisible = true
    }

    // 动画控制：退出
    LaunchedEffect(shouldDismiss) {
        if (shouldDismiss) {
            isContentVisible = false
            delay(bottomSheetData.duration.toLong())
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
            CompositionLocalProvider(LocalBottomSheetDismiss provides triggerDismiss) {
                SheetContent()
            }
        }
    }
}
