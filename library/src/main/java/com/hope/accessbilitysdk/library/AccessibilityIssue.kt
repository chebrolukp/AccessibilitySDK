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
        return "⚠ $title ($viewId)\n$description\n"
    }

    val viewId: String
        get() = try {
            view.resources.getResourceEntryName(view.id)
        } catch (e: Exception) {
            "no-id"
        }
}

interface AccessibilityDetector {
    fun check(view: View): AccessibilityIssue?
}
