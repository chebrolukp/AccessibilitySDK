package com.hope.accessbilitysdk.library

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportExporter(private val context: Context) {

    private val handlerThread = HandlerThread("ReportExporter").apply { start() }
    private val handler = Handler(handlerThread.looper)

    fun export(issues: List<AccessibilityIssue>, config: AccessibilityConfig) {
        if (!config.exportReports) return

        handler.post {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val activityName = context.javaClass.simpleName
            
            if (config.exportFormat == AccessibilityConfig.ExportFormat.JSON || 
                config.exportFormat == AccessibilityConfig.ExportFormat.BOTH) {
                exportJson(issues, activityName, timestamp)
            }
            
            if (config.exportFormat == AccessibilityConfig.ExportFormat.HTML || 
                config.exportFormat == AccessibilityConfig.ExportFormat.BOTH) {
                exportHtml(issues, activityName, timestamp)
            }
        }
    }

    private fun exportJson(issues: List<AccessibilityIssue>, activityName: String, timestamp: String) {
        val json = buildString {
            append("{\n")
            append("  \"activity\": \"$activityName\",\n")
            append("  \"timestamp\": \"$timestamp\",\n")
            append("  \"issueCount\": ${issues.size},\n")
            append("  \"issues\": [\n")
            issues.forEachIndexed { index, issue ->
                append("    {\n")
                append("      \"title\": \"${issue.title}\",\n")
                append("      \"description\": \"${issue.description}\",\n")
                append("      \"severity\": \"${issue.severity}\",\n")
                append("      \"viewId\": \"${issue.viewId}\",\n")
                append("      \"viewClass\": \"${issue.view.javaClass.name}\"\n")
                append("    }${if (index < issues.size - 1) "," else ""}\n")
            }
            append("  ]\n")
            append("}")
        }
        saveToFile(json, "report_${activityName}_$timestamp.json")
    }

    private fun exportHtml(issues: List<AccessibilityIssue>, activityName: String, timestamp: String) {
        val html = buildString {
            append("<!DOCTYPE html><html><head><title>A11y Report - $activityName</title>")
            append("<style>body{font-family:sans-serif;padding:20px;} .issue{border:1px solid #ccc;padding:10px;margin-bottom:10px;border-radius:5px;} .ERROR{border-left:5px solid red;} .WARNING{border-left:5px solid orange;}</style></head><body>")
            append("<h1>Accessibility Report: $activityName</h1>")
            append("<p>Generated: $timestamp | Issues: ${issues.size}</p>")
            issues.forEach { issue ->
                append("<div class=\"issue ${issue.severity}\">")
                append("<h3>${issue.title}</h3>")
                append("<p><strong>Severity:</strong> ${issue.severity}</p>")
                append("<p><strong>View ID:</strong> ${issue.viewId}</p>")
                append("<p>${issue.description}</p>")
                append("</div>")
            }
            append("</body></html>")
        }
        saveToFile(html, "report_${activityName}_$timestamp.html")
    }

    private fun saveToFile(content: String, fileName: String) {
        try {
            val dir = File(context.getExternalFilesDir(null), "A11yReports")
            if (!dir.exists()) dir.mkdirs()
            
            val file = File(dir, fileName)
            FileOutputStream(file).use { 
                it.write(content.toByteArray()) 
            }
            Log.d("AccessibilitySDK", "Report exported to: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("AccessibilitySDK", "Failed to export report", e)
        }
    }
}
