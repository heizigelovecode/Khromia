package heizige.kk.khromia.layout

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.ViewGroup
import androidx.activity.BackEventCompat
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AbstractComposeView
import androidx.core.view.children
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import java.util.UUID

@SuppressLint("ViewConstructor")
class BottomSheetLayout(
    private var onDismiss: () -> Unit,
    composeView: View,
    popupId: UUID,
    private val backDispatcher: OnBackPressedDispatcher?,
    private val enablePredictiveBack: Boolean
) : AbstractComposeView(composeView.context) {

    private val decorView = findOwner<Activity>(composeView.context)
        ?.window?.decorView as? ViewGroup
        ?: (composeView.rootView as ViewGroup)

    private var content: @Composable () -> Unit by mutableStateOf({})

    private var backCallback: androidx.activity.OnBackPressedCallback? = null

    init {
        id = android.R.id.content
        setViewTreeLifecycleOwner(composeView.findViewTreeLifecycleOwner())
        setViewTreeViewModelStoreOwner(composeView.findViewTreeViewModelStoreOwner())
        setViewTreeSavedStateRegistryOwner(composeView.findViewTreeSavedStateRegistryOwner())

        if (enablePredictiveBack) {
            backCallback = object : androidx.activity.OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onDismiss()
                }

                override fun handleOnBackProgressed(backEvent: BackEventCompat) {

                }
            }.also { callback ->
                val lifecycleOwner = composeView.findViewTreeLifecycleOwner()
                if (lifecycleOwner != null) {
                    backDispatcher?.addCallback(lifecycleOwner, callback)
                } else {
                    backDispatcher?.addCallback(callback)
                }
            }
        }
    }

    override var shouldCreateCompositionOnAttachedToWindow: Boolean = false
        private set

    fun show() {
        z = (decorView.children.maxOfOrNull { it.z } ?: 0f) + 1
        decorView.addView(
            this,
            MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        )

        requestFocus()
    }

    fun setContent(parent: CompositionContext, content: @Composable () -> Unit) {
        setParentCompositionContext(parent)
        this.content = content
        shouldCreateCompositionOnAttachedToWindow = true
    }

    fun updateContent(content: @Composable () -> Unit) {
        this.content = content
    }

    @Composable
    override fun Content() {
        content()
    }

    fun dismiss() {
        backCallback?.remove()
        backCallback = null
        setViewTreeLifecycleOwner(null)
        decorView.removeView(this)
    }

    override fun dispatchKeyEvent(event: android.view.KeyEvent): Boolean {
        return super.dispatchKeyEvent(event)
    }
}

private inline fun <reified T> findOwner(context: Context): T? {
    var innerContext = context
    while (innerContext is ContextWrapper) {
        if (innerContext is T) {
            return innerContext
        }
        innerContext = innerContext.baseContext
    }
    return null
}