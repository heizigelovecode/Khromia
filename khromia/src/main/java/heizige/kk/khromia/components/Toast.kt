package heizige.kk.khromia.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import heizige.kk.khromia.data.ToastManager
import heizige.kk.khromia.data.ToastModel
import heizige.kk.khromia.data.harmonizeWithPrimary
import heizige.kk.khromia.layout.FullscreenPopup
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

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

    Card(
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = modifier
            .padding(bottom = 48.dp)
            .systemBarsPadding()
            .heightIn(min = 48.dp)
            .widthIn(max = 300.dp)
            .alpha(0.8f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).fillMaxSize(),
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
            }
            Text(
                modifier = Modifier.weight(1f),
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
    val toastState = remember { mutableStateOf<ToastModel?>(null) }
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

            delay(newToast.duration)

            transitionState.targetState = false

            snapshotFlow { transitionState.isIdle && !transitionState.currentState }
                .filter { it }
                .first()

            isPopupActive = false
            toastState.value = null
        }
    }

    if (isPopupActive) {
        FullscreenPopup {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                AnimatedVisibility(
                    visibleState = transitionState,
                    enter = slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(durations.toInt())
                    ) + fadeIn(
                        animationSpec = tween(durations.toInt())
                    ) + scaleIn(
                        initialScale = 0.5f,
                        animationSpec = tween(durations.toInt())
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { it / 2 },
                        animationSpec = tween(durations.toInt())
                    ) + fadeOut(
                        animationSpec = tween(durations.toInt())
                    ) + scaleOut(
                        targetScale = 0.5f,
                        animationSpec = tween(durations.toInt())
                    )
                ) {
                    toastState.value?.let {
                        ToastCard(model = it)
                    }
                }
            }
        }
    }
}