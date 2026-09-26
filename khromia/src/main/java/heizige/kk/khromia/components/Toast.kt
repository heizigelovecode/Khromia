package heizige.kk.khromia.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import heizige.kk.khromia.data.CustomToastModel
import heizige.kk.khromia.data.ToastEntry
import heizige.kk.khromia.data.ToastManager
import heizige.kk.khromia.data.ToastModel
import heizige.kk.khromia.data.harmonizeWithPrimary
import heizige.kk.khromia.layout.FullscreenPopup
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

private val ToastShape = RoundedCornerShape(32.dp)

@Composable
private fun ToastCard(
    model: ToastModel,
    modifier: Modifier = Modifier
) {
    val containerColor = if (model.isError) {
        MaterialTheme.colorScheme.errorContainer.harmonizeWithPrimary()
    } else {
        MaterialTheme.colorScheme.inverseSurface.harmonizeWithPrimary()
    }

    val contentColor = if (model.isError) {
        MaterialTheme.colorScheme.onErrorContainer.harmonizeWithPrimary()
    } else {
        MaterialTheme.colorScheme.inverseOnSurface.harmonizeWithPrimary()
    }

    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = ToastShape,
        modifier = modifier
            .padding(bottom = 48.dp)
            .systemBarsPadding()
            .heightIn(min = 48.dp)
            .widthIn(max = 300.dp)
            .graphicsLayer {
                // 通过 graphicsLayer 强制渲染阴影，保证在 scale/fade 动画过程中阴影依然存在
                // （ImageToolbox materialShadow 的默认 6.dp 高度、黑色 ambient/spot）
                shadowElevation = 6.dp.toPx()
                shape = ToastShape
                clip = true
            }
            .alpha(0.95f) // ImageToolbox Toast 的 .alpha(0.95f) 应用在整个卡片图层
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (model.icon != null) {
                Icon(
                    imageVector = model.icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            } else if (model.painter != null) {
                Icon(
                    painter = model.painter,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = model.message,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun GlobalToastHost(durations: Long = 150L) {
    val toastState = remember { mutableStateOf<ToastEntry?>(null) }
    val transitionState = remember { MutableTransitionState(false) }
    var isPopupActive by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        ToastManager.toastFlow.collect { newToast ->
            toastState.value = newToast
            isPopupActive = true

            transitionState.targetState = false
            transitionState.targetState = true

            snapshotFlow { transitionState.isIdle && transitionState.currentState }
                .filter { it }
                .first()

            if (newToast.alwaysShow) {
                ToastManager.dismissRevision
                    .filter { it > newToast.dismissRevision }
                    .first()
            } else {
                delay(newToast.duration)
            }

            transitionState.targetState = false

            snapshotFlow { transitionState.isIdle && !transitionState.currentState }
                .filter { it }
                .first()

            isPopupActive = false
            toastState.value = null
        }
    }

    if (isPopupActive) {
        FullscreenPopup(passThroughTouches = true) {
            Box(
                contentAlignment = Alignment.BottomCenter
            ) {
                AnimatedVisibility(
                    visibleState = transitionState,
                    // ImageToolbox ToastDefaults.transition：
                    // enter = fadeIn(tween(300)) + scaleIn(spring(0.65f, MediumLow), origin 底部中点)
                    //         + slideInVertically(spring(StiffnessHigh)) { it / 2 }
                    // exit  = fadeOut(tween(250)) + slideOutVertically(tween(500)) { it / 2 }
                    //         + scaleOut(spring(MediumBouncy, MediumLow), origin 底部中点)
                    enter = fadeIn(
                        animationSpec = tween(300)
                    ) + scaleIn(
                        animationSpec = spring(
                            dampingRatio = 0.65f,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    ) + slideInVertically(
                        animationSpec = spring(
                            stiffness = Spring.StiffnessHigh
                        ),
                        initialOffsetY = { it / 2 }
                    ),
                    exit = fadeOut(
                        animationSpec = tween(250)
                    ) + slideOutVertically(
                        animationSpec = tween(500),
                        targetOffsetY = { it / 2 }
                    ) + scaleOut(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    )
                ) {
                    when (val toast = toastState.value) {
                        is ToastModel -> ToastCard(model = toast)
                        is CustomToastModel -> toast.content()
                        null -> Unit
                    }
                }
            }
        }
    }
}
