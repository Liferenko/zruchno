package com.example.a10101010.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a10101010.AppInfo
import com.example.a10101010.HomeViewModel
import com.example.a10101010.ui.theme.MonochromeTheme

@Composable
fun HomeRoute(viewModel: HomeViewModel = viewModel()) {
    val apps by viewModel.apps.collectAsState()
    HomeScreen(
        apps = apps,
        onLaunchApp = viewModel::launchApp
    )
}

@Composable
fun HomeScreen(
    apps: List<AppInfo>,
    onLaunchApp: (AppInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val labelStyle = if (maxWidth < 360.dp) {
            MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp, lineHeight = 20.sp)
        } else {
            MaterialTheme.typography.bodyLarge
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = maxWidth * 0.06f,
                vertical = maxHeight * 0.04f
            )
        ) {
            items(apps, key = { it.packageName }) { app ->
                Text(
                    text = app.label,
                    style = labelStyle,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLaunchApp(app) }
                        .padding(vertical = maxHeight * 0.012f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    MonochromeTheme {
        HomeScreen(
            apps = listOf(
                AppInfo("com.example.calculator", "Calculator"),
                AppInfo("com.example.settings", "Settings"),
                AppInfo("com.example.telephone", "Telephone")
            ),
            onLaunchApp = {}
        )
    }
}