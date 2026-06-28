package com.hope.accessbilitysdk

import android.app.Application
import com.hope.accessbilitysdk.library.AccessibilityMonitor
import com.hope.accessbilitysdk.library.AccessibilityConfig

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AccessibilityMonitor.install(this) {
            checkContrast(enabled = true)
            checkTouchTargets(enabled = true)
            checkContentDescriptions(enabled = true)
            checkDuplicates(enabled = true)
            checkCompose(enabled = true)
            checkFocusOrder(enabled = true)
            logToLogcat(enabled = true)
            showHighlightOverlay(enabled = true)
            showBorders(enabled = true)
            showTags(enabled = true)
            showSummaryOverlay(enabled = true)
            exportReports(enabled = true)
            exportFormat(format = AccessibilityConfig.ExportFormat.BOTH)
            failBuildOnErrors(enabled = false)
        }
    }
}
