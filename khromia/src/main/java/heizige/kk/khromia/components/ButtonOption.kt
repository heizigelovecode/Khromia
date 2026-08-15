package heizige.kk.khromia.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import heizige.kk.khromia.text.OptionText

@Composable
fun ButtonOption(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    iconContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.54f),
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.26f),
    shape: Shape = RoundedCornerShape(20.dp)
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .pressBounce(interactionSource)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(12.dp)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier
                .background(iconContainerColor, CircleShape)
                .padding(8.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.87f)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start, modifier = Modifier.weight(1f)) {
            OptionText(text = title)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.54f),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))
    }
}


@Composable
fun ButtonOption(
    modifier: Modifier = Modifier,
    painter: Painter,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    iconContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.54f),
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.26f),
    shape: Shape = RoundedCornerShape(20.dp)
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .pressBounce(interactionSource)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(12.dp)
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .background(iconContainerColor, CircleShape)
                .padding(8.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.87f)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start, modifier = Modifier.weight(1f)) {
            OptionText(text = title)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.54f),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))
    }
}
