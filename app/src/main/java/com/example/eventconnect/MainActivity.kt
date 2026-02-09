package com.example.eventconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.eventconnect.ui.navigation.NavGraph
import com.example.eventconnect.ui.theme.EventconnectTheme
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ REQUIRED FOR OPENSTREETMAP
        Configuration.getInstance().userAgentValue = packageName

        enableEdgeToEdge()

        setContent {
            EventconnectTheme {
                NavGraph()
            }
        }
    }
}
