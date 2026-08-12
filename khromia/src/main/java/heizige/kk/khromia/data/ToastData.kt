package heizige.kk.khromia.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.concurrent.atomic.AtomicLong

sealed interface ToastEntry {
    val duration: Long
    val alwaysShow: Boolean
    val dismissRevision: Long
}

data class ToastModel(
    val message: String,
    val icon: ImageVector? = null,
    val painter: Painter? = null,
    val isError: Boolean = false,
    override val duration: Long = ToastDuration.Short,
    override val alwaysShow: Boolean = false,
    override val dismissRevision: Long = ToastManager.currentDismissRevision()
) : ToastEntry

/**
 * A toast whose complete visual layout is supplied by the caller.
 * [content] is hosted at the same bottom-center position and uses the same
 * entrance/exit animation as a regular toast.
 */
data class CustomToastModel(
    override val duration: Long = ToastDuration.Short,
    override val alwaysShow: Boolean = false,
    override val dismissRevision: Long = ToastManager.currentDismissRevision(),
    val content: @Composable () -> Unit
) : ToastEntry

object ToastDuration {
    const val Short: Long = 1_200L
}

object ToastManager {
    private val _toastChannel = Channel<ToastEntry>(Channel.UNLIMITED)
    private val dismissCounter = AtomicLong(0L)
    private val _dismissRevision = MutableStateFlow(0L)
    val toastFlow = _toastChannel.receiveAsFlow()
    val dismissRevision = _dismissRevision.asStateFlow()

    fun show(model: ToastEntry) {
        _toastChannel.trySend(model)
    }

    fun dismiss() {
        _dismissRevision.value = dismissCounter.incrementAndGet()
    }

    internal fun currentDismissRevision(): Long = dismissCounter.get()
}
