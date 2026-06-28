package com.hope.accessbilitysdk.library

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View

object AccessibilityMonitor {

    private var config = AccessibilityConfig()
    private var scanner = AccessibilityScanner(config)
    private var exporter: ReportExporter? = null

    fun install(application: Application, block: (AccessibilityConfig.() -> Unit)? = null) {
        val newConfig = AccessibilityConfig()
        block?.invoke(newConfig)
        config = newConfig
        scanner = AccessibilityScanner(config)
        exporter = ReportExporter(application)

        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            
            override fun onActivityResumed(activity: Activity) {
                val rootView = activity.window.decorView.findViewById<View>(android.R.id.content)
                rootView.post {
                    val issues = scanner.scan(rootView)
                    if (issues.isNotEmpty()) {
                        val decorView = activity.window.decorView as android.view.ViewGroup
                        
                        // Add highlighting
                        if (config.showHighlightOverlay) {
                            val highlightOverlay = HighlightOverlay(activity, issues = issues)
                            decorView.addView(highlightOverlay, android.view.ViewGroup.LayoutParams(
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT
                            ))
                        }

                        // Add floating summary
                        if (config.showSummaryOverlay) {
                            val summaryOverlay = SummaryOverlay(activity, issues = issues)
                            decorView.addView(summaryOverlay, android.view.ViewGroup.LayoutParams(
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT
                            ))
                        }

                        // Export reports
                        exporter?.export(issues, config)
                    }
                }
            }

            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}
