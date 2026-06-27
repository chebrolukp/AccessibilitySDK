package com.hope.accessbilitysdk.library

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsOwner
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.semantics.AccessibilityAction
import kotlin.math.roundToInt

class ComposeScanner {

    fun scan(view: View): List<AccessibilityIssue> {
        val issues = mutableListOf<AccessibilityIssue>()
        
        // Find the AndroidComposeView which holds the semantics
        val androidComposeView = findAndroidComposeView(view)
        if (androidComposeView != null) {
            val semanticsOwner = getSemanticsOwner(androidComposeView)
            semanticsOwner?.rootSemanticsNode?.let { rootNode ->
                traverseSemantics(rootNode, androidComposeView, issues)
            }
        }
        
        return issues
    }

    private fun findAndroidComposeView(view: View): View? {
        if (view.javaClass.name.contains("AndroidComposeView")) {
            return view
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val found = findAndroidComposeView(view.getChildAt(i))
                if (found != null) return found
            }
        }
        return null
    }

    private fun getSemanticsOwner(view: View): SemanticsOwner? {
        return try {
            val method = view.javaClass.methods.firstOrNull { it.name == "getSemanticsOwner" }
            method?.invoke(view) as? SemanticsOwner
        } catch (e: Exception) {
            null
        }
    }

    private fun traverseSemantics(node: SemanticsNode, hostView: View, issues: MutableList<AccessibilityIssue>) {
        checkNode(node, hostView, issues)
        node.children.forEach { traverseSemantics(it, hostView, issues) }
    }

    private fun checkNode(node: SemanticsNode, hostView: View, issues: MutableList<AccessibilityIssue>) {
        val config = node.config
        
        // 1. Content Description Check
        val contentDescription = config.getOrNull(SemanticsProperties.ContentDescription)
        val text = config.getOrNull(SemanticsProperties.Text)
        val onClick = config.getOrNull(SemanticsActions.OnClick)
        
        // If it's a clickable element (button-like) but has no text or description
        if (onClick != null && contentDescription.isNullOrEmpty() && text.isNullOrEmpty()) {
            issues.add(AccessibilityIssue(
                view = hostView,
                title = "Compose: Missing Label",
                description = "Clickable Composable has no contentDescription or text.",
                severity = AccessibilityIssue.Severity.ERROR,
                customBounds = toAndroidRect(node.boundsInWindow)
            ))
        }

        // 2. Touch Target Check
        val bounds = node.boundsInWindow
        val density = hostView.resources.displayMetrics.density
        val widthDp = bounds.width / density
        val heightDp = bounds.height / density

        if (onClick != null && (widthDp < 48 || heightDp < 48)) {
            issues.add(AccessibilityIssue(
                view = hostView,
                title = "Compose: Small Touch Target",
                description = "Composable size: ${widthDp.toInt()}x${heightDp.toInt()}dp. Min 48x48dp recommended.",
                severity = AccessibilityIssue.Severity.WARNING,
                customBounds = toAndroidRect(bounds)
            ))
        }
    }

    private fun toAndroidRect(rect: androidx.compose.ui.geometry.Rect): Rect {
        return Rect(
            rect.left.roundToInt(),
            rect.top.roundToInt(),
            rect.right.roundToInt(),
            rect.bottom.roundToInt()
        )
    }
}
