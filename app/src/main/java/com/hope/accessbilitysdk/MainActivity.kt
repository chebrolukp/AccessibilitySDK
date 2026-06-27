package com.hope.accessbilitysdk

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.hope.accessbilitysdk.ui.theme.AccessbilitySDKTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AccessbilitySDKTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        Greeting(name = "Android")
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text("Low Contrast Test:")
                        
                        // Adding a low contrast TextView
                        AndroidView(
                            factory = { context ->
                                TextView(context).apply {
                                    text = "Low Contrast Text (Light Gray on White)"
                                    setTextColor(android.graphics.Color.LTGRAY)
                                    setBackgroundColor(android.graphics.Color.WHITE)
                                    setPadding(20, 20, 20, 20)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("The button below should trigger SDK warnings:")

        // Adding a 'bad' View to test the SDK
        AndroidView(
            factory = { context ->
                ImageButton(context).apply {
                    setImageResource(android.R.drawable.ic_menu_edit)
                    // Missing contentDescription (Critical Error)
                    // Intentionally small size (Warning)
                    layoutParams = android.view.ViewGroup.LayoutParams(
                        (30 * context.resources.displayMetrics.density).toInt(),
                        (30 * context.resources.displayMetrics.density).toInt()
                    )
                }
            },
            modifier = Modifier.size(30.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AccessbilitySDKTheme {
        Greeting("Android")
    }
}
