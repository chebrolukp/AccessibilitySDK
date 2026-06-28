package com.hope.accessbilitysdk.library

class AccessibilityConfig {
    var checkContrast: Boolean = true
    var checkTouchTargets: Boolean = true
    var checkContentDescriptions: Boolean = true
    var checkDuplicates: Boolean = true
    var checkCompose: Boolean = true
    var checkFocusOrder: Boolean = true
    
    var showHighlightOverlay: Boolean = true
    var showSummaryOverlay: Boolean = true
    var logToLogcat: Boolean = true
    var exportReports: Boolean = false
    var exportFormat: ExportFormat = ExportFormat.JSON

    enum class ExportFormat {
        JSON, HTML, BOTH
    }

    // DSL-friendly methods
    fun checkContrast(enabled: Boolean) { checkContrast = enabled }
    fun checkTouchTargets(enabled: Boolean) { checkTouchTargets = enabled }
    fun checkContentDescriptions(enabled: Boolean) { checkContentDescriptions = enabled }
    fun checkDuplicates(enabled: Boolean) { checkDuplicates = enabled }
    fun checkCompose(enabled: Boolean) { checkCompose = enabled }
    fun checkFocusOrder(enabled: Boolean) { checkFocusOrder = enabled }
    fun showHighlightOverlay(enabled: Boolean) { showHighlightOverlay = enabled }
    fun showSummaryOverlay(enabled: Boolean) { showSummaryOverlay = enabled }
    fun logToLogcat(enabled: Boolean) { logToLogcat = enabled }
    fun exportReports(enabled: Boolean) { exportReports = enabled }
    fun exportFormat(format: ExportFormat) { exportFormat = format }
}
