package heizige.kk.khromia.data

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

data class ToastModel(
    val message: String,
    val icon: ImageVector? = null,
    val isError: Boolean = false,
    val duration: Long = 1200L
)

object ToastManager {
    private val _toastChannel = Channel<ToastModel>(Channel.UNLIMITED)
    val toastFlow = _toastChannel.receiveAsFlow()
    fun show(model: ToastModel) {
        _toastChannel.trySend(model)
    }
}