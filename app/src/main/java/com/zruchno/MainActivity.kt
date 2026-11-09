package com.zruchno

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.zruchno.ui.HomeRoute
import com.zruchno.ui.theme.MonochromeTheme

class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MonochromeTheme {
                HomeRoute(viewModel = homeViewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.refresh()
    }
}