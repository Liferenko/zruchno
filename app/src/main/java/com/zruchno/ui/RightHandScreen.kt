package com.zruchno.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zruchno.AppInfo
import com.zruchno.DEFAULT_FONT_SIZE
import com.zruchno.HomeViewModel
import com.zruchno.orderHomeApps

@Composable
fun RightHandRoute(
    viewModel: HomeViewModel = viewModel(),
    onExit: () -> Unit
) {
    val handApps by viewModel.handApps.collectAsState()
    val filteredApps by viewModel.filteredApps.collectAsState()
    val query by viewModel.query.collectAsState()
    val hiddenPackages by viewModel.hiddenPackages.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()
    val displayedApps = if (query.isBlank()) handApps else filteredApps
    RightHandScreen(
        apps = displayedApps,
        hiddenPackages = hiddenPackages,
        searchActive = query.isNotBlank(),
        query = query,
        onQueryChange = viewModel::onQueryChange,
        onClearSearch = viewModel::clearSearch,
        onToggleHidden = viewModel::toggleHidden,
        onSetHidden = viewModel::setHidden,
        onLaunchApp = viewModel::launchApp,
        onExit = onExit,
        fontSize = fontSize,
        onFontRatio = viewModel::applyFontRatio
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RightHandScreen(
    apps: List<AppInfo>,
    hiddenPackages: Set<String>,
    searchActive: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onToggleHidden: (AppInfo) -> Unit,
    onSetHidden: (Set<String>, Boolean) -> Unit,
    onLaunchApp: (AppInfo) -> Unit,
    onExit: () -> Unit,
    fontSize: Float = DEFAULT_FONT_SIZE,
    onFontRatio: (Float) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchVisible by rememberSaveable { mutableStateOf(false) }
    var optionsFor by remember { mutableStateOf<AppInfo?>(null) }
    var selectionMode by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(emptySet<String>()) }
    var pullAccum by remember { mutableStateOf(0f) }
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

    BackHandler {
        if (selectionMode) {
            exitSelection()
        } else if (searchVisible) {
            collapseSearch()
        } else {
            onExit()
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .pinchFontSize(onFontRatio)
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .imePadding()
    ) {
        val boxWidth = maxWidth
        val boxHeight = maxHeight
        val labelStyle = MaterialTheme.typography.bodyLarge.copy(
            fontSize = fontSize.sp,
            lineHeight = (fontSize * 1.5f).sp
        )

        val thresholdPx = with(LocalDensity.current) { 64.dp.toPx() }
        val pullConnection = remember(thresholdPx) {
            object : NestedScrollConnection {
                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    val dy = available.y
                    return if (dy < 0f && source == NestedScrollSource.UserInput) {
                        pullAccum -= dy
                        if (pullAccum >= thresholdPx) searchVisible = true
                        Offset(0f, dy)
                    } else {
                        Offset.Zero
                    }
                }
            }
        }
        LaunchedEffect(searchVisible) {
            pullAccum = 0f
        }

        Column(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .nestedScroll(pullConnection)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = true,
                    contentPadding = PaddingValues(
                        start = boxWidth * 0.38f,
                        end = boxWidth * 0.04f,
                        top = boxHeight * 0.02f,
                        bottom = boxHeight * 0.02f
                    )
                ) {
                    val panelApps = if (searchActive) apps else orderHomeApps(apps, hiddenPackages)
                    items(panelApps, key = { it.packageName }) { app ->
                        val isHidden = !searchActive && app.packageName in hiddenPackages
                        val labelColor = if (isHidden) {
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
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
                                AppOptionsMenu(
                                    expanded = optionsFor == app,
                                    onDismissRequest = { optionsFor = null },
                                    labelStyle = labelStyle,
                                    isHidden = app.packageName in hiddenPackages,
                                    onToggleHidden = {
                                        onToggleHidden(app)
                                        optionsFor = null
                                    },
                                    onStartSelect = {
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

            AnimatedVisibility(visible = searchVisible) {
                AppSearchField(
                    query = query,
                    onQueryChange = onQueryChange,
                    onClear = { collapseSearch() },
                    autofocus = searchVisible,
                    textStyle = labelStyle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = boxWidth * 0.38f,
                            end = boxWidth * 0.04f,
                            top = boxHeight * 0.02f,
                            bottom = boxHeight * 0.02f
                        )
                )
            }

            AnimatedVisibility(visible = selectionMode) {
                val selectedApps = apps.filter { it.packageName in selected }
                val visibleSelected = selectedApps
                    .filter { it.packageName !in hiddenPackages }
                    .map { it.packageName }
                    .toSet()
                val hiddenSelected = selectedApps
                    .filter { it.packageName in hiddenPackages }
                    .map { it.packageName }
                    .toSet()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = boxWidth * 0.38f, end = boxWidth * 0.04f, top = boxHeight * 0.015f, bottom = boxHeight * 0.015f),
                    horizontalArrangement = Arrangement.spacedBy(boxWidth * 0.03f)
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
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
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
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
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

        if (!searchVisible && !selectionMode) {
            Text(
                text = "hm",
                style = labelStyle,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = boxWidth * 0.05f, bottom = boxHeight * 0.03f)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clickable(onClick = onExit)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}