package heizige.kk.khromia.components

import android.graphics.BlurMaskFilter
import android.graphics.Color as AndroidColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonDefaults.mediumContainerSize
import androidx.compose.material3.IconButtonDefaults.smallContainerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import heizige.kk.khromia.R
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SquareColorPicker(
    modifier: Modifier = Modifier,
    initialColor: Color,
    onColorChanged: (Color) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    val animHue        = remember { Animatable(0f) }
    val animSaturation = remember { Animatable(0f) }
    val animValue      = remember { Animatable(0f) }

    var lastTargetColor by remember { mutableStateOf(initialColor) }

    LaunchedEffect(initialColor) {
        if (initialColor != lastTargetColor) {
            lastTargetColor = initialColor

            val hsv = FloatArray(3)
            AndroidColor.colorToHSV(initialColor.toArgb(), hsv)

            launch { animHue.animateTo(hsv[0], tween(120)) }
            launch {
                animSaturation.animateTo(
                    hsv[1],
                    spring(stiffness = 900f, dampingRatio = 0.9f)
                )
            }
            launch {
                animValue.animateTo(
                    hsv[2],
                    spring(stiffness = 900f, dampingRatio = 0.9f)
                )
            }
        }
    }

    val dragSpec = spring<Float>(stiffness = 2500f, dampingRatio = 0.92f)
    val tapSpec  = spring<Float>(stiffness = 700f,  dampingRatio = 0.85f)

    fun updateColor(h: Float, s: Float, v: Float, isTap: Boolean) {
        val spec = if (isTap) tapSpec else dragSpec
        val cs = s.coerceIn(0f, 1f)
        val cv = v.coerceIn(0f, 1f)
        val ch = h.coerceIn(0f, 360f)
        val targetColor = Color(AndroidColor.HSVToColor(floatArrayOf(ch, cs, cv)))
        lastTargetColor = targetColor
        coroutineScope.launch {
            launch { animHue.animateTo(ch, spec) }
            launch { animSaturation.animateTo(cs, spec) }
            launch { animValue.animateTo(cv, spec) }
            onColorChanged(targetColor)
        }
    }

    fun generateRandomColor() {
        val randomHue = Random.nextFloat() * 360f
        val randomSat = Random.nextFloat()
        val randomVal = Random.nextFloat()
        updateColor(randomHue, randomSat, randomVal, true)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ── SV 方块 ──────────────────────────────────────────────
        Box(
            modifier = Modifier
                .weight(1f)
                .background(Color.Transparent)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        updateColor(
                            animHue.value,
                            (change.position.x / size.width).coerceIn(0f, 1f),
                            (1f - change.position.y / size.height).coerceIn(0f, 1f),
                            false
                        )
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        updateColor(
                            animHue.value,
                            (offset.x / size.width).coerceIn(0f, 1f),
                            (1f - offset.y / size.height).coerceIn(0f, 1f),
                            true
                        )
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize().background(Color.Transparent)) {

                drawRect(
                    color = Color(
                        AndroidColor.HSVToColor(floatArrayOf(animHue.value, 1f, 1f))
                    )
                )
                drawRect(
                    brush = Brush.horizontalGradient(
                        listOf(Color.White, Color.White.copy(alpha = 0f))
                    )
                )
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0f), Color.Black)
                    )
                )

                val drawS = animSaturation.value.coerceIn(0f, 1f)
                val drawV = animValue.value.coerceIn(0f, 1f)
                val cx = drawS * size.width
                val cy = (1f - drawV) * size.height
                val radius = 8.dp.toPx()
                val strokeWidth = 2.dp.toPx()

                drawIntoCanvas { canvas ->
                    val glowPaint = Paint().apply {
                        asFrameworkPaint().apply {
                            isAntiAlias = true
                            style = android.graphics.Paint.Style.STROKE
                            this.strokeWidth = strokeWidth
                            maskFilter = BlurMaskFilter(10.dp.toPx(), BlurMaskFilter.Blur.NORMAL)
                            setColor(android.graphics.Color.argb(180, 255, 255, 255))
                        }
                    }
                    canvas.drawCircle(Offset(cx, cy), radius, glowPaint)
                }

                drawCircle(
                    color = Color.White,
                    radius = radius,
                    center = Offset(cx, cy),
                    style = Stroke(width = strokeWidth)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // ── Hue 滑条 + FloatButton ───────────────────────────────
        // 使用 IntrinsicSize 让 Column 宽度适应内容
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(IntrinsicSize.Min),  // 让 Column 宽度由内部组件决定
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hue 滑条 - 宽度跟随 FloatingActionButton
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()  // 填满 Column 的宽度
                    .weight(1f)
                    .clip(CircleShape)
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            updateColor(
                                (change.position.y / size.height).coerceIn(0f, 1f) * 360f,
                                animSaturation.value,
                                animValue.value,
                                false
                            )
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            updateColor(
                                (offset.y / size.height).coerceIn(0f, 1f) * 360f,
                                animSaturation.value,
                                animValue.value,
                                true
                            )
                        }
                    }
            ) {
                val hueColors = (0..360).map {
                    Color(AndroidColor.HSVToColor(floatArrayOf(it.toFloat(), 1f, 1f)))
                }
                drawRect(brush = Brush.verticalGradient(hueColors))

                val cursorY = (animHue.value.coerceIn(0f, 360f) / 360f) * size.height

                drawIntoCanvas { canvas ->
                    val glowPaint = Paint().apply {
                        asFrameworkPaint().apply {
                            isAntiAlias = true
                            maskFilter = BlurMaskFilter(10.dp.toPx(), BlurMaskFilter.Blur.NORMAL)
                            setColor(android.graphics.Color.argb(180, 255, 255, 255))
                        }
                    }
                    canvas.drawRect(
                        left   = 0f,
                        top    = cursorY - 2.dp.toPx(),
                        right  = size.width,
                        bottom = cursorY + 2.dp.toPx(),
                        paint  = glowPaint
                    )
                }

                drawRect(
                    color     = Color.White,
                    topLeft   = Offset(0f, cursorY - 2.dp.toPx()),
                    size      = Size(size.width, 4.dp.toPx())
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            FilledIconButton(
                onClick = { generateRandomColor() },
                shapes = IconButtonDefaults.shapes(),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.87f)),
                modifier = Modifier.size(smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.bottom_sheet_random_color),
                )
            }
        }
    }
}