package com.hope.accessbilitysdk.library

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.TextView
import java.util.Locale
import kotlin.math.pow

class ContrastDetector : AccessibilityDetector {
    override fun check(view: View): AccessibilityIssue? {
        if (view is TextView && view.visibility == View.VISIBLE && view.text.isNotEmpty()) {
            val textColor = view.currentTextColor
            val backgroundColor = getBackgroundColor(view)

            if (backgroundColor != null) {
                val ratio = calculateContrastRatio(textColor, backgroundColor)
                
                if (ratio < 4.5) {
                    return AccessibilityIssue(
                        view = view,
                        title = "Low Contrast",
                        description = String.format(Locale.US, "Contrast ratio is %.2f:1. WCAG AA requires 4.5:1.", ratio),
                        severity = AccessibilityIssue.Severity.WARNING
                    )
                }
            }
        }
        return null
    }

    private fun calculateContrastRatio(foreground: Int, background: Int): Double {
        val l1 = calculateLuminance(foreground)
        val l2 = calculateLuminance(background)
        return (Math.max(l1, l2) + 0.05) / (Math.min(l1, l2) + 0.05)
    }

    private fun calculateLuminance(color: Int): Double {
        val red = Color.red(color) / 255.0
        val green = Color.green(color) / 255.0
        val blue = Color.blue(color) / 255.0

        val r = if (red <= 0.03928) red / 12.92 else ((red + 0.055) / 1.055).pow(2.4)
        val g = if (green <= 0.03928) green / 12.92 else ((green + 0.055) / 1.055).pow(2.4)
        val b = if (blue <= 0.03928) blue / 12.92 else ((blue + 0.055) / 1.055).pow(2.4)

        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    private fun getBackgroundColor(view: View): Int? {
        var currentView: View? = view
        while (currentView != null) {
            val background = currentView.background
            if (background is ColorDrawable) {
                val color = background.color
                if (Color.alpha(color) > 0) {
                    return color
                }
            }
            // If the view is the root or has no parent, stop
            val parent = currentView.parent
            if (parent is View) {
                currentView = parent
            } else {
                currentView = null
            }
        }
        return null
    }
}
