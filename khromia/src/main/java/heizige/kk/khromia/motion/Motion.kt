package heizige.kk.khromia.motion

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.IntSize

/**
 * Material 3 Expressive motion helpers.
 *
 * 统一从 [MaterialTheme.motionScheme] 取规格，避免各组件硬编码 tween/spring。
 * KodeHead 使用 `MotionScheme.expressive()`；Khromia demo 同样接入后行为一致。
 */

@Composable
@ReadOnlyComposable
fun <T> spatialSpec(): FiniteAnimationSpec<T> =
    MaterialTheme.motionScheme.defaultSpatialSpec()

@Composable
@ReadOnlyComposable
fun <T> fastSpatialSpec(): FiniteAnimationSpec<T> =
    MaterialTheme.motionScheme.fastSpatialSpec()

@Composable
@ReadOnlyComposable
fun <T> slowSpatialSpec(): FiniteAnimationSpec<T> =
    MaterialTheme.motionScheme.slowSpatialSpec()

@Composable
@ReadOnlyComposable
fun <T> effectsSpec(): FiniteAnimationSpec<T> =
    MaterialTheme.motionScheme.defaultEffectsSpec()

@Composable
@ReadOnlyComposable
fun <T> fastEffectsSpec(): FiniteAnimationSpec<T> =
    MaterialTheme.motionScheme.fastEffectsSpec()

@Composable
@ReadOnlyComposable
fun <T> slowEffectsSpec(): FiniteAnimationSpec<T> =
    MaterialTheme.motionScheme.slowEffectsSpec()

/** [animateContentSize] 默认用的空间规格。 */
@Composable
@ReadOnlyComposable
fun contentSizeSpec(): FiniteAnimationSpec<IntSize> = spatialSpec()

/** 列表/面板出现：淡入 + 轻微放大。 */
@Composable
fun fadeScaleIn(
    initialScale: Float = 0.92f,
): EnterTransition {
    val effects = effectsSpec<Float>()
    return fadeIn(animationSpec = effects) +
        scaleIn(animationSpec = effects, initialScale = initialScale)
}

/** 列表/面板消失：淡出 + 轻微缩小。 */
@Composable
fun fadeScaleOut(
    targetScale: Float = 0.92f,
): ExitTransition {
    val effects = fastEffectsSpec<Float>()
    return fadeOut(animationSpec = effects) +
        scaleOut(animationSpec = effects, targetScale = targetScale)
}

/** 副标题等纵向展开：expand + fade。 */
@Composable
fun expandFadeIn(): EnterTransition =
    expandVertically(animationSpec = spatialSpec()) +
        fadeIn(animationSpec = effectsSpec())

/** 副标题等纵向收起：shrink + fade。 */
@Composable
fun shrinkFadeOut(): ExitTransition =
    shrinkVertically(animationSpec = fastSpatialSpec()) +
        fadeOut(animationSpec = fastEffectsSpec())
