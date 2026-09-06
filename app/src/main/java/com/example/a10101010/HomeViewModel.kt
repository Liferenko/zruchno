package com.example.a10101010

import android.app.Application
import android.content.Context
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

fun orderHomeApps(apps: List<AppInfo>, hiddenPackages: Set<String>): List<AppInfo> {
    val (hidden, visible) = apps.partition { it.packageName in hiddenPackages }
    return visible + hidden
}

fun applyHiddenChange(current: Set<String>, packages: Set<String>, hidden: Boolean): Set<String> =
    if (hidden) current + packages else current - packages

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val packageManager = application.packageManager

    private val prefs = application.getSharedPreferences("launcher", Context.MODE_PRIVATE)
    private val hiddenPackagesKey = "hidden_packages"

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _hiddenPackages = MutableStateFlow<Set<String>>(
        prefs.getStringSet(hiddenPackagesKey, emptySet())?.toSet() ?: emptySet()
    )
    val hiddenPackages: StateFlow<Set<String>> = _hiddenPackages.asStateFlow()

    val homeApps: StateFlow<List<AppInfo>> =
        combine(_apps, _hiddenPackages) { apps, hidden -> orderHomeApps(apps, hidden) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

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

    fun toggleHidden(app: AppInfo) {
        val current = _hiddenPackages.value
        _hiddenPackages.value =
            if (app.packageName in current) current - app.packageName else current + app.packageName
        prefs.edit().putStringSet(hiddenPackagesKey, _hiddenPackages.value.toMutableSet()).apply()
    }

    fun setHidden(packages: Set<String>, hidden: Boolean) {
        val current = _hiddenPackages.value
        val updated = applyHiddenChange(current, packages, hidden)
        if (updated != current) {
            _hiddenPackages.value = updated
            prefs.edit().putStringSet(hiddenPackagesKey, updated.toMutableSet()).apply()
        }
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