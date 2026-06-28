package com.hope.accessbilitysdk.library

import android.util.Log
import android.view.View
import android.view.ViewGroup

class AccessibilityScanner(private val config: AccessibilityConfig) {

    private val composeScanner = ComposeScanner()
    private val duplicateDetector = DuplicateLabelDetector()
    private val focusOrderDetector = FocusOrderDetector()
    
    private val detectors = mutableListOf<AccessibilityDetector>().apply {
        if (config.checkContentDescriptions) add(ContentDescriptionDetector())
        if (config.checkTouchTargets) add(TouchTargetDetector())
        if (config.checkContrast) add(ContrastDetector())
    }

    fun scan(rootView: View): List<AccessibilityIssue> {
        val issues = mutableListOf<AccessibilityIssue>()
        traverse(rootView, issues)
        
        // Add duplicate label issues
        if (config.checkDuplicates) {
            issues.addAll(duplicateDetector.check(rootView))
        }

        // Add focus order issues
        if (config.checkFocusOrder) {
            issues.addAll(focusOrderDetector.check(rootView))
        }
        
        // Also scan Compose if present
        if (config.checkCompose) {
            issues.addAll(composeScanner.scan(rootView))
        }
        
        if (config.logToLogcat && issues.isNotEmpty()) {
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

        (view as? ViewGroup)?.let { group ->
            for (i in 0 until group.childCount) {
                traverse(group.getChildAt(i), issues)
            }
        }
    }
}
