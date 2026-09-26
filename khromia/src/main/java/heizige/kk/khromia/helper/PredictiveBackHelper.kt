package heizige.kk.khromia.helper

import androidx.activity.BackEventCompat
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState

@Composable
fun PredictiveBackHandler(
    backDispatcher: OnBackPressedDispatcher?,
    onProgress: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val currentOnProgress by rememberUpdatedState(onProgress)
    val currentOnDismiss by rememberUpdatedState(onDismiss)

    DisposableEffect(backDispatcher) {
        val callback = object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                currentOnDismiss()
            }

            override fun handleOnBackProgressed(backEvent: BackEventCompat) {
                currentOnProgress(backEvent.progress)
            }

            override fun handleOnBackCancelled() {
                // 手势取消时复位，避免内容卡在半途的位移状态。
                currentOnProgress(0f)
            }
        }

        backDispatcher?.addCallback(callback)

        onDispose {
            callback.remove()
        }
    }
}