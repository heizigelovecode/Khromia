package heizige.kk.khromia.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import heizige.kk.khromia.data.harmonizeWithPrimary


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TooltipScope.TextTooltip(text: String) {
    PlainTooltip(shape = CircleShape, containerColor = MaterialTheme.colorScheme.inverseSurface.harmonizeWithPrimary(), contentColor = MaterialTheme.colorScheme.surfaceVariant.harmonizeWithPrimary(), shadowElevation = 4.dp, modifier = Modifier.alpha(0.8f)) {1
        Text(text, Modifier.padding(4.dp))
    }
}