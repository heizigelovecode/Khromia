package heizige.kk.khromia.layout

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.ViewRootForInspector
import androidx.compose.ui.semantics.popup
import androidx.compose.ui.semantics.semantics
import androidx.core.view.children
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import java.util.UUID

@Composable
fun FullscreenPopup(
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
    /**
     * 为 true 时窗口仅包裹内容高度并贴底，空白区域触摸穿透到下层（适合 Toast）。
     */
    passThroughTouches: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val parentComposition = rememberCompositionContext()
    val currentContent by rememberUpdatedState(content)
    val popupId = rememberSaveable { UUID.randomUUID() }
    val popupLayout = remember(passThroughTouches) {
        PopupLayout(
            onDismiss = onDismiss,
            composeView = view,
            popupId = popupId,
            passThroughTouches = passThroughTouches
        ).apply {
            setContent(parentComposition) {
                Box(modifier.semantics { this.popup() }) {
                    currentContent()
                }
            }
        }
    }

    DisposableEffect(popupLayout) {
        popupLayout.show()
        popupLayout.updateParameters(
            onDismiss = onDismiss
        )
        onDispose {
            popupLayout.disposeComposition()
            // Remove the window
            popupLayout.dismiss()
        }
    }

    SideEffect {
        popupLayout.updateParameters(
            onDismiss = onDismiss
        )
    }
}

/**
 * The layout the popup uses to display its content.
 */
@SuppressLint("ViewConstructor")
private class PopupLayout(
    private var onDismiss: (() -> Unit)?,
    composeView: View,
    popupId: UUID,
    private val passThroughTouches: Boolean
) : AbstractComposeView(composeView.context),
    ViewRootForInspector {

    private val decorView = findOwner<Activity>(composeView.context)?.window?.decorView as ViewGroup

    override val subCompositionView: AbstractComposeView get() = this

    init {
        id = android.R.id.content
        setViewTreeLifecycleOwner(composeView.findViewTreeLifecycleOwner())
        setViewTreeViewModelStoreOwner(composeView.findViewTreeViewModelStoreOwner())
        setViewTreeSavedStateRegistryOwner(composeView.findViewTreeSavedStateRegistryOwner())
        // Set unique id for AbstractComposeView. This allows state restoration for the state
        // defined inside the Popup via rememberSaveable
        if (passThroughTouches) {
            isClickable = false
            isFocusable = false
            isFocusableInTouchMode = false
        }
    }

    private var content: @Composable () -> Unit by mutableStateOf({})

    override var shouldCreateCompositionOnAttachedToWindow: Boolean = false
        private set

    fun show() {
        // Place popup above all current views
        z = decorView.children.maxOf { it.z } + 1
        val params = if (passThroughTouches) {
            FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            )
        } else {
            MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
        decorView.addView(this, params)

        if (!passThroughTouches) {
            requestFocus()
        }
    }

    fun setContent(parent: CompositionContext, content: @Composable () -> Unit) {
        setParentCompositionContext(parent)
        this.content = content
        shouldCreateCompositionOnAttachedToWindow = true
    }

    @Composable
    override fun Content() {
        content()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (passThroughTouches) return false
        return super.onTouchEvent(event)
    }

    @Suppress("ReturnCount")
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && onDismiss != null) {
            if (keyDispatcherState == null) {
                return super.dispatchKeyEvent(event)
            }
            if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
                val state = keyDispatcherState
                state?.startTracking(event, this)
                return true
            } else if (event.action == KeyEvent.ACTION_UP) {
                val state = keyDispatcherState
                if (state != null && state.isTracking(event) && !event.isCanceled) {
                    onDismiss?.invoke()
                    return true
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    fun updateParameters(
        onDismiss: (() -> Unit)?
    ) {
        this.onDismiss = onDismiss
    }

    fun dismiss() {
        setViewTreeLifecycleOwner(null)
        decorView.removeView(this)
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
