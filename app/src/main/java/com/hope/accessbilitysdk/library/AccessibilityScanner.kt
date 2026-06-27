package com.hope.accessbilitysdk.library

import android.util.Log
import android.view.View
import android.view.ViewGroup

class AccessibilityScanner(private val detectors: List<AccessibilityDetector>) {

    private val composeScanner = ComposeScanner()

    fun scan(rootView: View): List<AccessibilityIssue> {
        val issues = mutableListOf<AccessibilityIssue>()
        traverse(rootView, issues)
        
        // Also scan Compose if present
        issues.addAll(composeScanner.scan(rootView))
        
        if (issues.isNotEmpty()) {
            Log.w("AccessibilitySDK", "--- Accessibility Report for ${rootView.context.javaClass.simpleName} ---")
            issues.forEach { Log.w("AccessibilitySDK", it.toString()) }
            Log.w("AccessibilitySDK", "------------------------------------------------------------")
        }
        return issues
    }

    private fun traverse(view: View, issues: MutableList<AccessibilityIssue>) {
        detectors.forEach { detector ->
            detector.check(view)?.let { issues.add(it) }
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                traverse(view.getChildAt(i), issues)
            }
        }
    }
}
