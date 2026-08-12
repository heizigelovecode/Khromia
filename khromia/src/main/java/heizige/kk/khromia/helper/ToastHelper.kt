package heizige.kk.khromia.helper

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import heizige.kk.khromia.data.CustomToastModel
import heizige.kk.khromia.data.ToastDuration
import heizige.kk.khromia.data.ToastManager
import heizige.kk.khromia.data.ToastModel

object Toast {
    const val SHORT_DURATION: Long = ToastDuration.Short

    fun show(message: String, isError: Boolean = false) {
        ToastManager.show(
            ToastModel(
                message = message,
                isError = isError
            )
        )
    }

    fun show(message: String, icon: ImageVector, isError: Boolean = false) {
        ToastManager.show(
            ToastModel(
                message = message,
                icon = icon,
                isError = isError
            )
        )
    }

    fun show(message: String, painter: Painter, isError: Boolean = false) {
        ToastManager.show(
            ToastModel(
                message = message,
                painter = painter,
                isError = isError
            )
        )
    }

    /**
     * Shows a caller-defined Compose layout using the global toast host.
     * The layout is displayed at the bottom center without an additional
     * Surface, padding, or size restriction.
     */
    fun showCustom(
        alwaysShow: Boolean = false,
        duration: Long = SHORT_DURATION,
        content: @Composable () -> Unit
    ) {
        ToastManager.show(
            CustomToastModel(
                duration = duration.requirePositiveDuration(),
                alwaysShow = alwaysShow,
                content = content
            )
        )
    }

    /** Closes the currently displayed persistent toast. */
    fun dismiss() {
        ToastManager.dismiss()
    }

    private fun Long.requirePositiveDuration(): Long {
        require(this > 0L) { "Toast duration must be greater than zero" }
        return this
    }
}
