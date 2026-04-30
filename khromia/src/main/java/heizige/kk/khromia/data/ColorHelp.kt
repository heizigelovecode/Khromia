package heizige.kk.khromia.data

import androidx.annotation.FloatRange
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

@Composable
fun Color.harmonizeWithPrimary(
    color: Color = MaterialTheme.colorScheme.primary,
    to: Float = 0.2f,
    @FloatRange(
        from = 0.0,
        to = 1.0
    ) fraction: Float = to
): Color {
    val thisArgb = toArgb()
    val primaryArgb = color.toArgb()
    val blendedArgb = ColorUtils.blendARGB(thisArgb, primaryArgb, fraction)
    return Color(blendedArgb)
}