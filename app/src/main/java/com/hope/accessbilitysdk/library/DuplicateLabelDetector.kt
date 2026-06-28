package com.hope.accessbilitysdk.library

import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class DuplicateLabelDetector {

    fun check(rootView: View): List<AccessibilityIssue> {
        val labelMap = mutableMapOf<String, MutableList<View>>()
        collectLabels(rootView, labelMap)

        val issues = mutableListOf<AccessibilityIssue>()
        labelMap.forEach { (label, views) ->
            if ((views.size > 1) && label.isNotBlank()) {
                views.forEach { view ->
                    issues.add(
                        AccessibilityIssue(
                            view = view,
                            title = "Duplicate Label",
                            description = "Multiple elements have the same label: '$label'. This can be confusing for screen reader users.",
                            severity = AccessibilityIssue.Severity.WARNING,
                        )
                    )
                }
            }
        }
        return issues
    }

    private fun collectLabels(view: View, labelMap: MutableMap<String, MutableList<View>>) {
        getLabel(view)?.let { label ->
            labelMap.getOrPut(label) { mutableListOf() }.add(view)
        }

        (view as? ViewGroup)?.let { group ->
            for (i in 0 until group.childCount) {
                collectLabels(group.getChildAt(i), labelMap)
            }
        }
    }

    private fun getLabel(view: View): String? {
        if (!view.isClickable) return null
        
        return view.contentDescription?.toString() 
            ?: (view as? TextView)?.text?.toString()
    }
}
