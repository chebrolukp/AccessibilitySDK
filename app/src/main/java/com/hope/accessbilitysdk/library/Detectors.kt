package com.hope.accessbilitysdk.library

import android.view.View
import androidx.core.view.isVisible
import android.widget.TextView

class ContentDescriptionDetector : AccessibilityDetector {
    override fun check(view: View): AccessibilityIssue? {
        // Flag anything clickable that doesn't have a label
        if (view.isClickable && (view.importantForAccessibility != View.IMPORTANT_FOR_ACCESSIBILITY_NO)) {
            val hasLabel = !view.contentDescription.isNullOrEmpty() || (view is TextView && view.text.isNotEmpty())
            
            if (!hasLabel) {
                return AccessibilityIssue(
                    view = view,
                    title = "Missing Label",
                    description = "${view.javaClass.simpleName} is clickable but has no contentDescription or text. Screen readers will just say 'unlabelled button'.",
                    severity = AccessibilityIssue.Severity.ERROR,
                )
            }
        }
        return null
    }
}

class TouchTargetDetector : AccessibilityDetector {
    override fun check(view: View): AccessibilityIssue? {
        if (view.isClickable && view.isVisible) {
            val density = view.resources.displayMetrics.density
            val widthDp = view.width / density
            val heightDp = view.height / density

            // Only report if it has a non-zero size (might not be laid out yet)
            if (widthDp > 0 && heightDp > 0 && (widthDp < 48 || heightDp < 48)) {
                return AccessibilityIssue(
                    view = view,
                    title = "Small Touch Target",
                    description = "Current size: ${widthDp.toInt()}x${heightDp.toInt()}dp. Recommended minimum is 48x48dp.",
                    severity = AccessibilityIssue.Severity.WARNING
                )
            }
        }
        return null
    }
}
