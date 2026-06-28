package com.hope.accessbilitysdk.library

import android.app.Activity
import android.view.View
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class AccessibilityTestRule(private val config: AccessibilityConfig = AccessibilityConfig()) : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                // Run the original test
                base.evaluate()

                // After test finishes, find the current activity and scan it
                val scanner = AccessibilityScanner(config)
                var currentActivity: Activity? = null
                
                InstrumentationRegistry.getInstrumentation().runOnMainSync {
                    val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
                    if (resumedActivities.iterator().hasNext()) {
                        currentActivity = resumedActivities.iterator().next()
                    }
                }

                currentActivity?.let { activity ->
                    val rootView = activity.window.decorView.findViewById<View>(android.R.id.content)
                    val issues = scanner.scan(rootView)
                    
                    if (issues.isNotEmpty()) {
                        val errorCount = issues.count { it.severity == AccessibilityIssue.Severity.ERROR }
                        if (errorCount > 0) {
                            throw AssertionError("Accessibility Check Failed: Found $errorCount errors on ${activity.javaClass.simpleName}.\n" +
                                issues.joinToString("\n") { it.toString() })
                        }
                    }
                }
            }
        }
    }
}
