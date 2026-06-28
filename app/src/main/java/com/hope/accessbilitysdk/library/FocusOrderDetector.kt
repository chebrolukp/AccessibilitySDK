package com.hope.accessbilitysdk.library

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible

class FocusOrderDetector {

    fun check(rootView: View): List<AccessibilityIssue> {
        val issues = mutableListOf<AccessibilityIssue>()
        val focusableViews = mutableListOf<View>()
        collectFocusableViews(rootView, focusableViews)

        if (focusableViews.isEmpty()) return emptyList()

        // Check for Focus Cycles and Traps
        val visited = mutableSetOf<View>()
        var current: View? = focusableViews.first()
        val path = mutableListOf<View>()

        while (current != null && (current !in visited)) {
            visited.add(current)
            path.add(current)
            @Suppress("WrongConstant")
            current = current.focusSearch(View.FOCUS_FORWARD)
        }

        if (current != null && (current in visited)) {
            // Focus cycle detected
            issues.add(
                AccessibilityIssue(
                    view = current,
                    title = "Focus Cycle Detected",
                    description = "Keyboard focus trapped in a loop. Navigation might never reach other parts of the screen.",
                    severity = AccessibilityIssue.Severity.ERROR,
                )
            )
        }

        // Check for unreachable focusable views
        focusableViews.forEach { view ->
            if (view !in visited && view.isVisible) {
                issues.add(
                    AccessibilityIssue(
                        view = view,
                        title = "Unreachable Focus",
                        description = "This view is focusable but cannot be reached via standard 'Forward' navigation (e.g., Tab key).",
                        severity = AccessibilityIssue.Severity.WARNING,
                    )
                )
            }
        }

        return issues
    }

    private fun collectFocusableViews(view: View, focusableViews: MutableList<View>) {
        if (view.isFocusable && view.isVisible) {
            focusableViews.add(view)
        }

        (view as? ViewGroup)?.let { group ->
            for (i in 0 until group.childCount) {
                collectFocusableViews(group.getChildAt(i), focusableViews)
            }
        }
    }
}
