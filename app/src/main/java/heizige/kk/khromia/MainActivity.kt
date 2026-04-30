package heizige.kk.khromia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import heizige.kk.khromia.components.BottomSheet
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
        Text("test")
    }

        var showBottomSheet by remember { mutableStateOf(false) }

        Button(onClick = { showBottomSheet = true }) {
            Text("显示 BottomSheet")
        }

        BottomSheet(
            visible = showBottomSheet,
            onDismiss = { showBottomSheet = false },
            enablePredictiveBack = true  // 开启预测返回
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("标题", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text("支持预测返回手势！")
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showBottomSheet = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("关闭")
                }
            }
        }
}
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KhromiaTheme {
        Greeting("Android")
    }
}