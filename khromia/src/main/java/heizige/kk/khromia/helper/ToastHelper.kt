package heizige.kk.khromia.helper

import androidx.compose.ui.graphics.vector.ImageVector
import heizige.kk.khromia.data.ToastManager
import heizige.kk.khromia.data.ToastModel

object Toast {
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
}