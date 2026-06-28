package com.hope.accessbilitysdk.library

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import java.lang.ref.WeakReference

object AccessibilityMonitor {

    private var config = AccessibilityConfig()
    private var scanner = AccessibilityScanner(config)
    private var exporterRef: WeakReference<ReportExporter>? = null

    fun install(application: Application, block: (AccessibilityConfig.() -> Unit)? = null) {
        val newConfig = AccessibilityConfig()
        block?.invoke(newConfig)
        config = newConfig
        scanner = AccessibilityScanner(config)
        exporterRef = WeakReference(ReportExporter(application))

        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            
            override fun onActivityResumed(activity: Activity) {
                val rootView = activity.window.decorView.findViewById<View>(android.R.id.content)
                rootView.post {
                    val issues = scanner.scan(rootView)
                    if (issues.isNotEmpty()) {
                        val decorView = activity.window.decorView as ViewGroup
                        
                        // Add highlighting
                        if (config.showHighlightOverlay && (config.showBorders || config.showTags)) {
                            val highlightOverlay = HighlightOverlay(activity, issues = issues, config = config)
                            decorView.addView(
                                highlightOverlay,
                                ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                )
                            )
                        }

                        // Add floating summary
                        if (config.showSummaryOverlay) {
                            val summaryOverlay = SummaryOverlay(activity, issues = issues)
                            decorView.addView(
                                summaryOverlay,
                                ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                )
                            )
                        }

                        // Export reports
                        exporterRef?.get()?.export(issues, config)
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
