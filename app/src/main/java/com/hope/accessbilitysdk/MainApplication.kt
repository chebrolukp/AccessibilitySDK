package com.hope.accessbilitysdk

import android.app.Application
import com.hope.accessbilitysdk.library.AccessibilityMonitor

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AccessibilityMonitor.install(this) {
            checkContrast(true)
            checkTouchTargets(true)
            logToLogcat(true)
            showHighlightOverlay(true)
            showSummaryOverlay(true)
        }
    }
}
