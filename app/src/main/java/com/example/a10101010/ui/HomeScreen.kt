package com.example.a10101010.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
    val homeApps by viewModel.homeApps.collectAsState()
    val filteredApps by viewModel.filteredApps.collectAsState()
    val query by viewModel.query.collectAsState()
    val hiddenPackages by viewModel.hiddenPackages.collectAsState()
    val displayedApps = if (query.isBlank()) homeApps else filteredApps
    HomeScreen(
        apps = displayedApps,
        hiddenPackages = hiddenPackages,
        searchActive = query.isNotBlank(),
        query = query,
        onQueryChange = viewModel::onQueryChange,
        onClearSearch = viewModel::clearSearch,
        onToggleHidden = viewModel::toggleHidden,
        onSetHidden = viewModel::setHidden,
        onLaunchApp = viewModel::launchApp
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    apps: List<AppInfo>,
    hiddenPackages: Set<String>,
    searchActive: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onToggleHidden: (AppInfo) -> Unit,
    onSetHidden: (Set<String>, Boolean) -> Unit,
    onLaunchApp: (AppInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchVisible by rememberSaveable { mutableStateOf(false) }
    var optionsFor by remember { mutableStateOf<AppInfo?>(null) }
    var selectionMode by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(emptySet<String>()) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    fun collapseSearch() {
        searchVisible = false
        onClearSearch()
        keyboard?.hide()
    }

    fun exitSelection() {
        selectionMode = false
        selected = emptySet()
        optionsFor = null
    }

    fun toggleSelection(packageName: String) {
        selected = if (packageName in selected) selected - packageName else selected + packageName
    }

    fun applyBulk(affectedPackages: Set<String>, hidden: Boolean) {
        onSetHidden(affectedPackages, hidden)
        selected = selected - affectedPackages
        if (selected.isEmpty()) exitSelection()
    }

    LaunchedEffect(searchVisible) {
        if (searchVisible) {
            focusRequester.requestFocus()
            keyboard?.show()
        }
    }

    BackHandler(enabled = searchVisible || selectionMode) {
        if (selectionMode) {
            exitSelection()
        } else {
            collapseSearch()
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
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
                        val isHidden = !searchActive && app.packageName in hiddenPackages
                        val labelColor = if (isHidden) {
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        }
                        if (selectionMode) {
                            val isSelected = app.packageName in selected
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { toggleSelection(app.packageName) }
                                    .padding(vertical = boxHeight * 0.012f)
                            ) {
                                Text(
                                    text = if (isSelected) "\u00bb" else "\u00b7",
                                    style = labelStyle,
                                    color = labelColor,
                                    modifier = Modifier.padding(end = boxWidth * 0.03f)
                                )
                                Text(
                                    text = app.label,
                                    style = labelStyle,
                                    color = labelColor
                                )
                            }
                        } else {
                            Box(Modifier.fillMaxWidth()) {
                                Text(
                                    text = app.label,
                                    style = labelStyle,
                                    color = labelColor,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .combinedClickable(
                                            onClick = { onLaunchApp(app) },
                                            onLongClick = { optionsFor = app }
                                        )
                                        .padding(vertical = boxHeight * 0.012f)
                                )
                                DropdownMenu(
                                    expanded = optionsFor == app,
                                    onDismissRequest = { optionsFor = null },
                                    containerColor = MaterialTheme.colorScheme.background,
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                ) {
                                    val isHidden = app.packageName in hiddenPackages
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = if (isHidden) "unhide" else "hide",
                                                style = labelStyle,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                        },
                                        onClick = {
                                            onToggleHidden(app)
                                            optionsFor = null
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "select",
                                                style = labelStyle,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                        },
                                        onClick = {
                                            selected = selected + app.packageName
                                            selectionMode = true
                                            optionsFor = null
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(visible = selectionMode) {
                val selectedApps = apps.filter { it.packageName in selected }
                val visibleSelected = selectedApps.filter { it.packageName !in hiddenPackages }.map { it.packageName }.toSet()
                val hiddenSelected = selectedApps.filter { it.packageName in hiddenPackages }.map { it.packageName }.toSet()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = boxWidth * 0.06f, vertical = boxHeight * 0.02f),
                    horizontalArrangement = Arrangement.spacedBy(boxWidth * 0.04f)
                ) {
                    Text(
                        text = "${selected.size} selected",
                        style = labelStyle,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "hide",
                        style = labelStyle,
                        color = if (visibleSelected.isNotEmpty()) {
                            MaterialTheme.colorScheme.onBackground
                        } else {
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        },
                        modifier = Modifier.clickable(enabled = visibleSelected.isNotEmpty()) {
                            applyBulk(visibleSelected, true)
                        }
                    )
                    Text(
                        text = "unhide",
                        style = labelStyle,
                        color = if (hiddenSelected.isNotEmpty()) {
                            MaterialTheme.colorScheme.onBackground
                        } else {
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        },
                        modifier = Modifier.clickable(enabled = hiddenSelected.isNotEmpty()) {
                            applyBulk(hiddenSelected, false)
                        }
                    )
                    Text(
                        text = "\u00d7",
                        style = labelStyle,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.clickable { exitSelection() }
                    )
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
            hiddenPackages = setOf("com.example.telephone"),
            searchActive = false,
            query = "",
            onQueryChange = {},
            onClearSearch = {},
            onToggleHidden = {},
            onSetHidden = { _, _ -> },
            onLaunchApp = {}
        )
    }
}