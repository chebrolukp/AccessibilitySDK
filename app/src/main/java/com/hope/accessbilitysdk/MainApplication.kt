package com.hope.accessbilitysdk

import android.app.Application
import com.hope.accessbilitysdk.library.AccessibilityMonitor
import com.hope.accessbilitysdk.library.AccessibilityConfig

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AccessibilityMonitor.install(this) {
            checkContrast(true)
            checkTouchTargets(true)
            logToLogcat(true)
            showHighlightOverlay(true)
            showSummaryOverlay(true)
            exportReports(true)
            exportFormat(AccessibilityConfig.ExportFormat.BOTH)
        }
    }
}
