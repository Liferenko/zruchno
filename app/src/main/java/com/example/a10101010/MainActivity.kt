package com.example.a10101010

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.a10101010.ui.HomeRoute
import com.example.a10101010.ui.theme.MonochromeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MonochromeTheme {
                HomeRoute()
            }
        }
    }
}