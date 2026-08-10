package heizige.kk.khromia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import heizige.kk.khromia.components.AnimatedFloatingActionButton
import heizige.kk.khromia.components.ButtonOption
import heizige.kk.khromia.components.EditDialog
import heizige.kk.khromia.components.EditFieldConfig
import heizige.kk.khromia.components.ExpandableOptionItem
import heizige.kk.khromia.components.GlobalToastHost
import heizige.kk.khromia.components.OptionItem
import heizige.kk.khromia.components.PrimaryBottomSheet
import heizige.kk.khromia.components.SquareColorPicker
import heizige.kk.khromia.helper.Toast
import heizige.kk.khromia.helper.fadingEdge
import heizige.kk.khromia.text.OptionsText
import heizige.kk.khromia.ui.theme.KhromiaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KhromiaTheme {
                GlobalToastHost()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        AnimatedFloatingActionButton(onClick = {
                            Toast.show(stringResource(R.string.fab_clicked))
                        }) {
                            Icon(Icons.Filled.Edit, contentDescription = null)
                        }
                    }
                ) { innerPadding ->
                    SettingsScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // ── General ──
        OptionsText(stringResource(R.string.section_general))
        Spacer(modifier = Modifier.height(8.dp))

        var wifiOn by remember { mutableStateOf(true) }
        OptionItem(
            imageVector = Icons.Filled.Wifi,
            title = stringResource(R.string.opt_wifi),
            subtitle = stringResource(R.string.opt_wifi_desc),
            checked = wifiOn,
            onCheckedChange = { wifiOn = it }
        )
        Spacer(modifier = Modifier.height(8.dp))

        var darkOn by remember { mutableStateOf(false) }
        OptionItem(
            imageVector = Icons.Filled.DarkMode,
            title = stringResource(R.string.opt_dark_mode),
            subtitle = stringResource(R.string.opt_dark_mode_desc),
            checked = darkOn,
            onCheckedChange = { darkOn = it }
        )
        Spacer(modifier = Modifier.height(8.dp))

        ButtonOption(
            imageVector = Icons.Filled.Language,
            title = stringResource(R.string.opt_language),
            subtitle = stringResource(R.string.opt_language_desc),
            onClick = { Toast.show(stringResource(R.string.toast_language)) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Notifications ──
        OptionsText(stringResource(R.string.section_notifications))
        Spacer(modifier = Modifier.height(8.dp))

        var notifExpanded by remember { mutableStateOf(false) }
        ExpandableOptionItem(
            imageVector = Icons.Filled.Notifications,
            title = stringResource(R.string.opt_notifications),
            subtitle = stringResource(R.string.opt_notifications_desc),
            checked = notifExpanded,
            onCheckedChange = { notifExpanded = it }
        ) {
            var soundOn by remember { mutableStateOf(true) }
            var vibrateOn by remember { mutableStateOf(true) }

            OptionItem(
                imageVector = Icons.Filled.VolumeUp,
                title = stringResource(R.string.opt_sound),
                checked = soundOn,
                onCheckedChange = { soundOn = it }
            )
            Spacer(modifier = Modifier.height(4.dp))
            OptionItem(
                imageVector = Icons.Filled.Accessibility,
                title = stringResource(R.string.opt_vibrate),
                checked = vibrateOn,
                onCheckedChange = { vibrateOn = it }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Display ──
        OptionsText(stringResource(R.string.section_display))
        Spacer(modifier = Modifier.height(8.dp))

        ExpandableOptionItem(
            imageVector = Icons.Filled.Palette,
            title = stringResource(R.string.opt_display),
            subtitle = stringResource(R.string.opt_display_desc),
            initiallyExpanded = false
        ) {
            var fontSize by remember { mutableStateOf("Medium") }
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Small", "Medium", "Large").forEach { size ->
                    TextButton(
                        onClick = { fontSize = size },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = size,
                            color = if (fontSize == size) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Dialogs & Popups ──
        OptionsText(stringResource(R.string.section_dialogs))
        Spacer(modifier = Modifier.height(8.dp))

        var showBottomSheet by remember { mutableStateOf(false) }
        var showEditDialog by remember { mutableStateOf(false) }
        var editResult by remember { mutableStateOf("") }

        ButtonOption(
            imageVector = Icons.Filled.Info,
            title = stringResource(R.string.opt_show_toast),
            onClick = { Toast.show(stringResource(R.string.toast_hello), Icons.Filled.Check, isError = false) }
        )
        Spacer(modifier = Modifier.height(8.dp))

        ButtonOption(
            imageVector = Icons.Filled.Settings,
            title = stringResource(R.string.opt_show_bottom_sheet),
            onClick = { showBottomSheet = true }
        )
        Spacer(modifier = Modifier.height(8.dp))

        ButtonOption(
            imageVector = Icons.Filled.Edit,
            title = stringResource(R.string.opt_show_edit_dialog),
            subtitle = editResult.ifEmpty { stringResource(R.string.subtitle_waiting) },
            onClick = { showEditDialog = true }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Color Picker ──
        OptionsText(stringResource(R.string.section_color_picker))
        Spacer(modifier = Modifier.height(8.dp))

        var pickedColor by remember { mutableStateOf(Color(0xFF6750A4)) }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.26f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SquareColorPicker(
                    initialColor = pickedColor,
                    onColorChanged = { pickedColor = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(pickedColor)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = String.format(
                        "#%02X%02X%02X",
                        (pickedColor.red * 255).toInt(),
                        (pickedColor.green * 255).toInt(),
                        (pickedColor.blue * 255).toInt()
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Fading Edge ──
        OptionsText(stringResource(R.string.section_fading_edge))
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.26f))
                .fadingEdge(top = 40.dp, bottom = 40.dp, strength = 1.0f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            repeat(10) { index ->
                Text(
                    stringResource(R.string.fading_edge_line, index),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                .fadingEdge(left = 40.dp, right = 40.dp, strength = 0.7f)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                stringResource(R.string.fading_edge_horizontal),
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── About ──
        OptionsText(stringResource(R.string.section_about))
        Spacer(modifier = Modifier.height(8.dp))

        ButtonOption(
            imageVector = Icons.Filled.Share,
            title = stringResource(R.string.opt_share),
            subtitle = stringResource(R.string.opt_share_desc),
            onClick = { Toast.show(stringResource(R.string.toast_share)) }
        )
        Spacer(modifier = Modifier.height(8.dp))

        ButtonOption(
            imageVector = Icons.Filled.Info,
            title = stringResource(R.string.opt_about),
            subtitle = stringResource(R.string.opt_about_desc),
            onClick = { Toast.show("Khromia v1.0", isError = false) }
        )

        Spacer(modifier = Modifier.height(80.dp))

        // ── Bottom Sheet ──
        PrimaryBottomSheet(
            visible = showBottomSheet,
            title = stringResource(R.string.sheet_title),
            imageVector = Icons.Filled.Settings,
            onDismiss = { showBottomSheet = false }
        ) { onDismiss ->
            Column(modifier = Modifier.padding(16.dp)) {
                var sheetNotif by remember { mutableStateOf(true) }
                OptionItem(
                    imageVector = Icons.Filled.Notifications,
                    title = stringResource(R.string.opt_notifications),
                    checked = sheetNotif,
                    onCheckedChange = { sheetNotif = it }
                )
                Spacer(modifier = Modifier.height(8.dp))

                var sheetAuto by remember { mutableStateOf(false) }
                OptionItem(
                    imageVector = Icons.Filled.Accessibility,
                    title = stringResource(R.string.opt_auto_update),
                    checked = sheetAuto,
                    onCheckedChange = { sheetAuto = it }
                )
                Spacer(modifier = Modifier.height(8.dp))

                ButtonOption(
                    imageVector = Icons.Filled.Person,
                    title = stringResource(R.string.opt_account),
                    subtitle = stringResource(R.string.opt_account_desc),
                    onClick = {
                        Toast.show(stringResource(R.string.toast_account))
                        onDismiss()
                    }
                )
            }
        }

        // ── Edit Dialog ──
        EditDialog(
            visible = showEditDialog,
            title = stringResource(R.string.dialog_title),
            fields = listOf(
                EditFieldConfig(
                    label = stringResource(R.string.field_name),
                    initialValue = "",
                    placeholder = stringResource(R.string.placeholder_name),
                    maxLength = 10
                ),
                EditFieldConfig(
                    label = stringResource(R.string.field_age),
                    initialValue = "",
                    placeholder = stringResource(R.string.placeholder_age),
                    keyboardType = KeyboardType.Number,
                    range = 0.0..150.0
                ),
                EditFieldConfig(
                    label = stringResource(R.string.field_email),
                    initialValue = "",
                    placeholder = stringResource(R.string.placeholder_email),
                    onValidate = { value ->
                        if (value.isBlank()) stringResource(R.string.error_email_empty)
                        else if (!value.contains("@")) stringResource(R.string.error_email_invalid)
                        else null
                    }
                )
            ),
            onDismiss = { showEditDialog = false },
            onConfirm = { results ->
                editResult = stringResource(R.string.result_format, results[0], results[1], results[2])
                showEditDialog = false
                Toast.show(stringResource(R.string.toast_saved))
            }
        )
    }
}
