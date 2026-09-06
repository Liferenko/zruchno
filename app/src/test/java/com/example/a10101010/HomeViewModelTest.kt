package com.example.a10101010

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeViewModelTest {

    private val apps = listOf(
        AppInfo("com.example.calendar", "Calendar"),
        AppInfo("com.example.camera", "Camera"),
        AppInfo("com.example.messages", "Messages"),
        AppInfo("com.example.phone", "Phone")
    )

    @Test
    fun emptyQueryKeepsFullList() {
        assertEquals(apps, filterApps(apps, ""))
    }

    @Test
    fun whitespaceQueryKeepsFullList() {
        assertEquals(apps, filterApps(apps, "   "))
    }

    @Test
    fun caseInsensitiveSubstringMatches() {
        assertEquals(
            listOf(apps[0], apps[1]),
            filterApps(apps, "Ca")
        )
        assertEquals(
            listOf(apps[3]),
            filterApps(apps, "PHONE")
        )
    }

    @Test
    fun queryIsTrimmed() {
        assertEquals(listOf(apps[3]), filterApps(apps, " phone "))
    }

    @Test
    fun noMatchYieldsEmptyList() {
        assertTrue(filterApps(apps, "zzz").isEmpty())
    }

    @Test
    fun filterKeepsHiddenAppsInResults() {
        val hidden = setOf("com.example.camera")
        val filtered = filterApps(apps, "ca")
        assertEquals(listOf(apps[0], apps[1]), filtered)
    }

    @Test
    fun orderHomeAppsKeepsEverythingWhenNothingHidden() {
        assertEquals(apps, orderHomeApps(apps, emptySet()))
    }

    @Test
    fun orderHomeAppsMovesHiddenToBottom() {
        val hidden = setOf("com.example.phone")
        assertEquals(
            listOf(apps[0], apps[1], apps[2], apps[3]),
            orderHomeApps(apps, hidden)
        )
    }

    @Test
    fun orderHomeAppsGroupsHiddenAfterVisible() {
        val hidden = setOf("com.example.calendar", "com.example.phone")
        assertEquals(
            listOf(apps[1], apps[2], apps[0], apps[3]),
            orderHomeApps(apps, hidden)
        )
    }

    @Test
    fun orderHomeAppsUnhideRestoresAlphabeticalPosition() {
        val hidden = setOf("com.example.phone")
        val unhidden = hidden - "com.example.phone"
        assertEquals(apps, orderHomeApps(apps, unhidden))
        assertEquals(apps[3], orderHomeApps(apps, unhidden)[3])
    }

    @Test
    fun applyHiddenChangeBulkHidesAll() {
        val current = setOf("com.example.calendar")
        val updated = applyHiddenChange(current, setOf("com.example.camera", "com.example.phone"), true)
        assertEquals(setOf("com.example.calendar", "com.example.camera", "com.example.phone"), updated)
    }

    @Test
    fun applyHiddenChangeBulkUnhidesAll() {
        val current = setOf("com.example.calendar", "com.example.camera", "com.example.phone")
        val updated = applyHiddenChange(current, setOf("com.example.calendar", "com.example.phone"), false)
        assertEquals(setOf("com.example.camera"), updated)
    }

    @Test
    fun applyHiddenChangeUnaffectedPackagesUntouched() {
        val current = setOf("com.example.calendar", "com.example.camera")
        val updated = applyHiddenChange(current, setOf("com.example.phone"), true)
        assertEquals(setOf("com.example.calendar", "com.example.camera", "com.example.phone"), updated)
    }
}