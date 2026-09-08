package com.zruchno.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zruchno.AppInfo
import com.zruchno.DEFAULT_FONT_SIZE
import com.zruchno.HomeViewModel
import com.zruchno.ui.theme.MonochromeTheme

@Composable
fun Modifier.pinchFontSize(onFontRatio: (Float) -> Unit): Modifier {
    val currentOnFontRatio by rememberUpdatedState(onFontRatio)
    return pointerInput(Unit) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false)
            var previousDistance = 0f
            do {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                val pressedChanges = event.changes.filter { it.pressed }
                if (pressedChanges.size >= 2) {
                    val distance =
                        (pressedChanges[0].position - pressedChanges[1].position).getDistance()
                    if (previousDistance > 0f && distance != previousDistance) {
                        currentOnFontRatio(distance / previousDistance)
                    }
                    previousDistance = distance
                    pressedChanges.forEach { it.consume() }
                } else {
                    previousDistance = 0f
                }
            } while (event.changes.any { it.pressed })
        }
    }
}

@Composable
fun Modifier.dismissOnEmptyTap(onDismiss: () -> Unit): Modifier {
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val touchSlop = LocalViewConfiguration.current.touchSlop
    return pointerInput(touchSlop) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            val downPosition = down.position
            var up: PointerInputChange? = null
            var cancelled = false
            while (up == null && !cancelled) {
                val event = awaitPointerEvent()
                if (event.changes.count { it.pressed } > 1) {
                    cancelled = true
                    break
                }
                val change = event.changes.firstOrNull { it.id == down.id } ?: continue
                when {
                    change.isConsumed -> cancelled = true
                    !change.pressed -> up = change
                    (change.position - downPosition).getDistance() >= touchSlop -> cancelled = true
                }
            }
            if (up != null && !up.isConsumed) {
                currentOnDismiss()
            }
        }
    }
}

@Composable
fun HomeRoute(viewModel: HomeViewModel = viewModel()) {
    val homeApps by viewModel.homeApps.collectAsState()
    val filteredApps by viewModel.filteredApps.collectAsState()
    val query by viewModel.query.collectAsState()
    val hiddenPackages by viewModel.hiddenPackages.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()
    val context = LocalContext.current
    var rightHand by rememberSaveable { mutableStateOf(false) }
    if (rightHand) {
        RightHandRoute(
            viewModel = viewModel,
            onExit = { rightHand = false }
        )
    } else {
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
            onLaunchApp = { viewModel.launchApp(it, context) },
            onOpenAppInfo = { viewModel.openAppInfo(it, context) },
            onRequestUninstall = { viewModel.requestUninstall(it, context) },
            fontSize = fontSize,
            onFontRatio = viewModel::applyFontRatio,
            cornerToggleText = "rh",
            onCornerToggle = { rightHand = true }
        )
    }
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
    onOpenAppInfo: (AppInfo) -> Unit = {},
    onRequestUninstall: (AppInfo) -> Unit = {},
    fontSize: Float = DEFAULT_FONT_SIZE,
    onFontRatio: (Float) -> Unit = {},
    modifier: Modifier = Modifier,
    cornerToggleText: String = "",
    onCornerToggle: () -> Unit = {}
) {
    var searchVisible by rememberSaveable { mutableStateOf(false) }
    var optionsFor by remember { mutableStateOf<AppInfo?>(null) }
    var selectionMode by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(emptySet<String>()) }
    var appToUninstall by remember { mutableStateOf<AppInfo?>(null) }
    val keyboard = LocalSoftwareKeyboardController.current
    val selfPackageName = LocalContext.current.packageName

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

    BackHandler(enabled = searchVisible || selectionMode || appToUninstall != null) {
        when {
            appToUninstall != null -> appToUninstall = null
            selectionMode -> exitSelection()
            else -> collapseSearch()
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .pinchFontSize(onFontRatio)
            .dismissOnEmptyTap {
                if (optionsFor != null) optionsFor = null
                if (searchVisible) collapseSearch()
            }
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        val boxWidth = maxWidth
        val boxHeight = maxHeight
        val labelStyle = MaterialTheme.typography.bodyLarge.copy(
            fontSize = fontSize.sp,
            lineHeight = (fontSize * 1.5f).sp
        )

        Column(Modifier.fillMaxSize()) {
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
                            horizontal = boxWidth * 0.06f,
                            vertical = boxHeight * 0.02f
                        )
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
                                    },
                                    showUninstall = !app.isSystemApp && app.packageName != selfPackageName,
                                    onOpenInfo = {
                                        onOpenAppInfo(app)
                                        optionsFor = null
                                    },
                                    onUninstall = {
                                        appToUninstall = app
                                        optionsFor = null
                                    }
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(visible = appToUninstall != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = boxWidth * 0.06f, vertical = boxHeight * 0.02f),
                    horizontalArrangement = Arrangement.spacedBy(boxWidth * 0.04f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "uninstall ${appToUninstall?.label ?: ""}?",
                        style = labelStyle,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "yes",
                        style = labelStyle,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.clickable {
                            val app = appToUninstall ?: return@clickable
                            appToUninstall = null
                            onRequestUninstall(app)
                        }
                    )
                    Text(
                        text = "no",
                        style = labelStyle,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.clickable { appToUninstall = null }
                    )
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

        if (cornerToggleText.isNotEmpty() && !searchVisible && !selectionMode) {
            Text(
                text = cornerToggleText,
                style = labelStyle,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = boxWidth * 0.06f, bottom = boxHeight * 0.03f)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clickable(onClick = onCornerToggle)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
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

@Composable
fun AppSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    autofocus: Boolean,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(autofocus) {
        if (autofocus) {
            focusRequester.requestFocus()
            keyboard?.show()
        }
    }
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.focusRequester(focusRequester),
        placeholder = {
            Text(
                text = "search",
                style = textStyle,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        textStyle = textStyle,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onClear() }),
        trailingIcon = {
            Text(
                text = "\u00d7",
                style = textStyle,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .clickable(onClick = onClear)
                    .padding(8.dp)
            )
        }
    )
}

@Composable
fun AppOptionsMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    labelStyle: TextStyle,
    isHidden: Boolean,
    onToggleHidden: () -> Unit,
    onStartSelect: () -> Unit,
    showUninstall: Boolean,
    onOpenInfo: () -> Unit,
    onUninstall: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onBackground
        )
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = if (isHidden) "unhide" else "hide",
                    style = labelStyle,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            onClick = onToggleHidden
        )
        DropdownMenuItem(
            text = {
                Text(
                    text = "select",
                    style = labelStyle,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            onClick = onStartSelect
        )
        DropdownMenuItem(
            text = {
                Text(
                    text = "info",
                    style = labelStyle,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            onClick = onOpenInfo
        )
        if (showUninstall) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "uninstall",
                        style = labelStyle,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                onClick = onUninstall
            )
        }
    }
}