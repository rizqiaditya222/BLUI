package com.kotlin.blui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kotlin.blui.presentation.navigation.NavigationGraph
import com.kotlin.blui.ui.theme.BluiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BluiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavigationGraph()
                }
            }
        }
    }
}