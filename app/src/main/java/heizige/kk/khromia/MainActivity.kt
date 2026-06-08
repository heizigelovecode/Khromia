package heizige.kk.khromia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import heizige.kk.khromia.components.EditDialog
import heizige.kk.khromia.components.EditFieldConfig
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.graphics.Color
import heizige.kk.khromia.components.SquareColorPicker
import heizige.kk.khromia.helper.fadingEdge
import heizige.kk.khromia.components.GlobalToastHost
import heizige.kk.khromia.helper.Toast
import heizige.kk.khromia.ui.theme.KhromiaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KhromiaTheme {
                GlobalToastHost()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier)  {
        Button(onClick = {
            Toast.show("hello")
        }){
            Text(stringResource(R.string.test_toast))
        }

        var showBottomSheet by remember { mutableStateOf(false) }

        Button(onClick = { showBottomSheet = true }) {
            Text(stringResource(R.string.show_bottom_sheet))
        }

        var showEditDialog by remember { mutableStateOf(false) }
        var resultText by remember { mutableStateOf("") }
        val defaultWaiting = stringResource(R.string.waiting_input)
        val displayResult = resultText.ifEmpty { defaultWaiting }

        Button(onClick = { showEditDialog = true }) {
            Text(stringResource(R.string.open_edit_dialog))
        }

        Text(text = displayResult, modifier = Modifier.padding(top = 8.dp))

        Spacer(modifier = Modifier.height(24.dp))

        var pickedColor by remember { mutableStateOf(Color.Blue) }
        Text(text = "选色器测试:", style = MaterialTheme.typography.titleMedium)
        SquareColorPicker(
            initialColor = pickedColor,
            onColorChanged = { pickedColor = it },
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        )
        
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(pickedColor, CircleShape)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "边缘模糊测试 (Fading Edge):", style = MaterialTheme.typography.titleMedium)
        
        // 垂直滚动 + 强力度淡化
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(vertical = 8.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .fadingEdge(top = 40.dp, bottom = 40.dp, strength = 1.0f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            repeat(10) { index ->
                Text("垂直淡化示例行 $index", modifier = Modifier.padding(vertical = 4.dp))
            }
        }

        // 水平淡化 + 中等力度
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .fadingEdge(left = 40.dp, right = 40.dp, strength = 0.7f)
                .padding(24.dp)
        ) {
            Text("这是一个水平边缘淡化的测试文本，左右两侧都有平滑的过渡效果。", maxLines = 1)
        }

        val nameLabel = stringResource(R.string.field_name)
        val ageLabel = stringResource(R.string.field_age)
        val customLabel = stringResource(R.string.field_custom)
        val namePlaceholder = stringResource(R.string.placeholder_name)
        val agePlaceholder = stringResource(R.string.placeholder_age)
        val customError = stringResource(R.string.error_must_pass)
        val saveSuccessMsg = stringResource(R.string.save_success)
        val resultFormat = stringResource(R.string.result_format)

        EditDialog(
            visible = showEditDialog,
            title = stringResource(R.string.user_info_edit),
            fields = listOf(
                EditFieldConfig(
                    label = nameLabel,
                    initialValue = "张三",
                    placeholder = namePlaceholder,
                    maxLength = 5
                ),
                EditFieldConfig(
                    label = ageLabel,
                    initialValue = "25",
                    placeholder = agePlaceholder,
                    keyboardType = KeyboardType.Number,
                    range = 0.0..150.0
                ),
                EditFieldConfig(
                    label = customLabel,
                    onValidate = { if (it != "pass") customError else null }
                )
            ),
            onDismiss = { showEditDialog = false },
            onConfirm = { results ->
                resultText = resultFormat.format(results[0], results[1], results[2])
                showEditDialog = false
                Toast.show(saveSuccessMsg)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KhromiaTheme {
        Greeting("Android")
    }
}