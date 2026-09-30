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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import heizige.kk.khromia.helper.PredictiveBackHandler
import android.view.WindowManager
import java.util.concurrent.atomic.AtomicBoolean

private const val DialogMotionDurationMillis = 260

/**
 * 官方 [Dialog] 窗口 + 统一进/退场动效的弹窗宿主。
 *
 * 相比自建的 `FullscreenPopup`，这里把弹窗放进真正的 Dialog 窗口：返回键、点击外部、焦点、
 * 无障碍与 IME 都交给系统；动效本身（遮罩 0.32 淡入淡出、自下而上滑入 + 缩放淡入、跟手返回）
 * 与旧实现保持一致，因此调用方无需感知宿主变化。
 *
 * 窗口层面做了两处归零，避免系统默认行为与自带动效叠加：
 * - 系统背景遮罩（主题默认约 0.6）置 0，由 [scrimAlpha] 的自绘遮罩接管；
 * - 窗口进出场动画置 0，由内容层的 [AnimatedVisibility] 接管。
 *
 * 保持挂载并用 [visible] 驱动；[onDismissRequest] 在退场动画结束之后回调。
 */
@Composable
fun AnimatedDialogWindow(
    visible: Boolean = true,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    enablePredictiveBack: Boolean = true,
    scrimColor: Color = Color.Black,
    scrimAlpha: Float = 0.32f,
    content: @Composable () -> Unit,
) {
    val visibilityState = remember { MutableTransitionState(false) }
    var hasBeenShown by remember { mutableStateOf(false) }
    var isDismissing by remember { mutableStateOf(false) }
    var backProgress by remember { mutableFloatStateOf(0f) }
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)

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

    val dialogProperties = remember(properties) {
        // 全屏 + edge-to-edge：内容滑入的起点在视口之外，自绘遮罩也能盖住状态栏区域。
        DialogProperties(
            dismissOnBackPress = properties.dismissOnBackPress,
            dismissOnClickOutside = properties.dismissOnClickOutside,
            securePolicy = properties.securePolicy,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
            windowTitle = properties.windowTitle,
            windowType = properties.windowType,
            windowToken = properties.windowToken,
        )
    }

    Dialog(
        onDismissRequest = ::requestDismiss,
        properties = dialogProperties,
    ) {
        // LocalView 的父级即 DialogLayout（DialogWindowProvider），用于关闭系统遮罩与窗口动画。
        val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
        // 只需应用一次：动画过程中每次重组都改 Window attributes 会触发整窗 relayout。
        val windowTuned = remember(dialogWindow) { AtomicBoolean(false) }
        SideEffect {
            val window = dialogWindow ?: return@SideEffect
            if (!windowTuned.compareAndSet(false, true)) return@SideEffect
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            window.attributes = window.attributes.apply {
                dimAmount = 0f
                windowAnimations = 0
            }
        }

        val dimAlpha by animateFloatAsState(
            targetValue = if (visibilityState.targetState) scrimAlpha else 0f,
            animationSpec = tween(DialogMotionDurationMillis),
            label = "AnimatedDialogScrim"
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(scrimColor.copy(alpha = dimAlpha))
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
                val backDispatcher =
                    LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
                PredictiveBackHandler(
                    backDispatcher = backDispatcher,
                    onProgress = { backProgress = it },
                    onDismiss = ::requestDismiss
                )
            }

            // AnimatedVisibility 填满视口（imePadding 让内容随键盘上移），滑入起点即视口下沿。
            AnimatedVisibility(
                visibleState = visibilityState,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(DialogMotionDurationMillis)
                ) + fadeIn(animationSpec = tween(DialogMotionDurationMillis)) + scaleIn(
                    initialScale = 0.92f,
                    animationSpec = tween(DialogMotionDurationMillis)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(DialogMotionDurationMillis)
                ) + fadeOut(animationSpec = tween(DialogMotionDurationMillis)) + scaleOut(
                    targetScale = 0.92f,
                    animationSpec = tween(DialogMotionDurationMillis)
                ),
                modifier = Modifier.fillMaxSize().imePadding()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = modifier.graphicsLayer {
                            translationY = backProgress * size.height * 0.45f
                            scaleX = 1f - backProgress * 0.06f
                            scaleY = 1f - backProgress * 0.06f
                            alpha = 1f - backProgress * 0.3f
                        }
                    ) {
                        content()
                    }
                }
            }
        }
    }
}
