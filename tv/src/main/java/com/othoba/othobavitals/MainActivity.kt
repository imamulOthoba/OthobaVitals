package com.othoba.othobavitals

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.tv.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.tv.material3.Surface
import com.othoba.othobavitals.dashboard.DashboardScreen
import com.othoba.othobavitals.ui.theme.OthobaVitalsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        
        // Keeps the screen awake strictly while this Activity is open in the foreground
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // We will hold the splash screen until we explicitly release it
        splashScreen.setKeepOnScreenCondition { true }
        
        setContent {
            OthobaVitalsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {
                    DashboardScreen(onLoaded = {
                        // Release the splash screen when WebView is fully loaded!
                        splashScreen.setKeepOnScreenCondition { false }
                    })
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OthobaVitalsTheme {
        Greeting("Android")
    }
}