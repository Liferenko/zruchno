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
}