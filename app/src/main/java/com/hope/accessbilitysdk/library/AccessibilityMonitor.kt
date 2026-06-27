package com.hope.accessbilitysdk.library

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View

object AccessibilityMonitor {

    private val scanner = AccessibilityScanner(
        listOf(
            ContentDescriptionDetector(),
            TouchTargetDetector(),
            ContrastDetector()
        )
    )

    fun install(application: Application) {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            
            override fun onActivityResumed(activity: Activity) {
                val rootView = activity.window.decorView.findViewById<View>(android.R.id.content)
                rootView.post {
                    val issues = scanner.scan(rootView)
                    if (issues.isNotEmpty()) {
                        val overlay = HighlightOverlay(activity, issues = issues)
                        val decorView = activity.window.decorView as android.view.ViewGroup
                        decorView.addView(overlay, android.view.ViewGroup.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT
                        ))
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
