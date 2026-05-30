package heizige.kk.khromia.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun OptionsText(text: String) {
    Text(text = text, lineHeight = 20.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.54f), maxLines = 1, fontSize = 14.sp)
}

@Composable
fun OptionText(text: String) {
    Text(text = text, lineHeight = 24.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.87f), maxLines = 1, fontSize = 16.sp)
}