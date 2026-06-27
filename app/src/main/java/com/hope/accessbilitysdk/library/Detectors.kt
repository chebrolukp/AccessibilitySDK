package com.hope.accessbilitysdk.library

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView

class ContentDescriptionDetector : AccessibilityDetector {
    override fun check(view: View): AccessibilityIssue? {
        if (view is ImageView || view is ImageButton) {
            if (view.contentDescription.isNullOrEmpty() && view.importantForAccessibility != View.IMPORTANT_FOR_ACCESSIBILITY_NO) {
                return AccessibilityIssue(
                    view = view,
                    title = "Missing Content Description",
                    description = "${view.javaClass.simpleName} is missing a content description.",
                    severity = AccessibilityIssue.Severity.ERROR
                )
            }
        }
        return null
    }
}

class TouchTargetDetector : AccessibilityDetector {
    override fun check(view: View): AccessibilityIssue? {
        if (view.isClickable && view.visibility == View.VISIBLE) {
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
