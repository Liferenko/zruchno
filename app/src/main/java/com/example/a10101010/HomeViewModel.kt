package com.example.a10101010

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

fun filterApps(apps: List<AppInfo>, query: String): List<AppInfo> {
    val trimmed = query.trim()
    return if (trimmed.isEmpty()) {
        apps
    } else {
        apps.filter { it.label.contains(trimmed, ignoreCase = true) }
    }
}

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val packageManager = application.packageManager

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val filteredApps: StateFlow<List<AppInfo>> =
        combine(_apps, _query) { apps, query -> filterApps(apps, query) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        refresh()
    }

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun clearSearch() {
        _query.value = ""
    }

    fun refresh() {
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfos = packageManager.queryIntentActivities(
            launcherIntent,
            PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong())
        )
        _apps.value = resolveInfos
            .mapNotNull { info ->
                val label = info.loadLabel(packageManager).toString()
                AppInfo(packageName = info.activityInfo.packageName, label = label)
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
    }

    fun launchApp(app: AppInfo) {
        val launchIntent = packageManager.getLaunchIntentForPackage(app.packageName) ?: return
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        getApplication<Application>().startActivity(launchIntent)
    }
}