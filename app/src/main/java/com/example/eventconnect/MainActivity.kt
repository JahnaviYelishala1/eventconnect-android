package com.example.eventconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.eventconnect.ui.SplashScreen
import com.example.eventconnect.ui.navigation.NavGraph
import com.example.eventconnect.ui.theme.EventconnectTheme
import kotlinx.coroutines.delay
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ REQUIRED FOR OPENSTREETMAP
        Configuration.getInstance().userAgentValue = packageName

        enableEdgeToEdge()

        setContent {
            EventconnectTheme {
                var showSplash by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    delay(2000) // Show splash for 2 seconds
                    showSplash = false
                }

                if (showSplash) {
                    SplashScreen()
                } else {
                    NavGraph()
                }
            }
        }
    }
}
