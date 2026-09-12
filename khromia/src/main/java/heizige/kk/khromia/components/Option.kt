package heizige.kk.khromia.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import heizige.kk.khromia.text.OptionText

@Composable
fun OptionItem(
    modifier: Modifier = Modifier,
    painter: Painter,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.26f),
    shape: Shape = RoundedCornerShape(20.dp),
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .pressBounce(interactionSource)
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(12.dp)
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.54f), CircleShape)
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.54f),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        content()
    }
}

@Composable
fun OptionItem(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.26f),
    shape: Shape = RoundedCornerShape(20.dp),
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .pressBounce(interactionSource)
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(12.dp)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.54f), CircleShape)
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.54f),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        content()
    }
}


@Composable
fun OptionItem(
    modifier: Modifier = Modifier,
    painter: Painter,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.26f),
    shape: Shape = RoundedCornerShape(20.dp)
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .pressBounce(interactionSource)
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .clickable(interactionSource = interactionSource, indication = null) { onCheckedChange(!checked) }
            .padding(12.dp)
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.54f), CircleShape)
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.54f),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        OptionSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun OptionItem(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.26f),
    shape: Shape = RoundedCornerShape(20.dp)
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .pressBounce(interactionSource)
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .clickable(interactionSource = interactionSource, indication = null) { onCheckedChange(!checked) }
            .padding(12.dp)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.54f), CircleShape)
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.54f),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        OptionSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}


/** 插槽版 OptionItem：供宿主用 composable 内容（标题/副标题/前后置）复用组件库交互与样式。 */
@Composable
fun OptionItem(
    modifier: Modifier = Modifier,
    title: String = "",
    onClick: () -> Unit,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.26f),
    leadingContent: (@Composable () -> Unit)? = null,
    overlineContent: (@Composable () -> Unit)? = null,
    titleContent: (@Composable () -> Unit)? = null,
    supportingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .pressBounce(interactionSource)
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(12.dp),
    ) {
        if (leadingContent != null) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.54f), CircleShape)
                    .padding(8.dp),
            ) {
                leadingContent()
            }
            Spacer(modifier = Modifier.width(12.dp))
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(1f),
        ) {
            overlineContent?.invoke()
            if (titleContent != null) titleContent() else Text(title)
            supportingContent?.invoke()
        }
        if (trailingContent != null) {
            Spacer(modifier = Modifier.width(12.dp))
            trailingContent()
        }
    }
}

// composableSlotLoaded
