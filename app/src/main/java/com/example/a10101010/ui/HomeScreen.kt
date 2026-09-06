package com.example.a10101010.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a10101010.AppInfo
import com.example.a10101010.HomeViewModel
import com.example.a10101010.ui.theme.MonochromeTheme

@Composable
fun HomeRoute(viewModel: HomeViewModel = viewModel()) {
    val apps by viewModel.filteredApps.collectAsState()
    val query by viewModel.query.collectAsState()
    HomeScreen(
        apps = apps,
        query = query,
        onQueryChange = viewModel::onQueryChange,
        onClearSearch = viewModel::clearSearch,
        onLaunchApp = viewModel::launchApp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    apps: List<AppInfo>,
    query: String,
    onQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onLaunchApp: (AppInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchVisible by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    fun collapseSearch() {
        searchVisible = false
        onClearSearch()
        keyboard?.hide()
    }

    LaunchedEffect(searchVisible) {
        if (searchVisible) {
            focusRequester.requestFocus()
            keyboard?.show()
        }
    }

    BackHandler(enabled = searchVisible) {
        collapseSearch()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val boxWidth = maxWidth
        val boxHeight = maxHeight
        val labelStyle = if (boxWidth < 360.dp) {
            MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp, lineHeight = 20.sp)
        } else {
            MaterialTheme.typography.bodyLarge
        }

        Column(Modifier.fillMaxSize()) {
            AnimatedVisibility(visible = searchVisible) {
                TextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .padding(horizontal = boxWidth * 0.06f, vertical = boxHeight * 0.02f),
                    placeholder = {
                        Text(
                            text = "search",
                            style = labelStyle,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    textStyle = labelStyle,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { collapseSearch() }),
                    trailingIcon = {
                        Text(
                            text = "\u00d7",
                            style = labelStyle,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .clickable { collapseSearch() }
                                .padding(8.dp)
                        )
                    }
                )
            }

            PullToRefreshBox(
                isRefreshing = false,
                onRefresh = { searchVisible = true },
                indicator = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = boxWidth * 0.06f,
                        vertical = boxHeight * 0.04f
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
                                .padding(vertical = boxHeight * 0.012f)
                        )
                    }
                }
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
            query = "",
            onQueryChange = {},
            onClearSearch = {},
            onLaunchApp = {}
        )
    }
}