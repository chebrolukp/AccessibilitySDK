package com.hope.accessbilitysdk.library

import android.view.View

data class AccessibilityIssue(
    val view: View,
    val title: String,
    val description: String,
    val severity: Severity = Severity.WARNING,
    val customBounds: android.graphics.Rect? = null
) {
    enum class Severity {
        INFO, WARNING, ERROR
    }

    override fun toString(): String {
        val viewId = try {
            view.resources.getResourceEntryName(view.id)
        } catch (e: Exception) {
            "no-id"
        }
        return "⚠ $title ($viewId)\n$description\n"
    }
}

interface AccessibilityDetector {
    fun check(view: View): AccessibilityIssue?
}
