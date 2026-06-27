package com.hope.accessbilitysdk.library

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

class SummaryOverlay(context: Context, private val issues: List<AccessibilityIssue>) : FrameLayout(context) {

    init {
        val errorCount = issues.count { it.severity == AccessibilityIssue.Severity.ERROR }
        val warningCount = issues.count { it.severity == AccessibilityIssue.Severity.WARNING }

        val backgroundDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 40f
            setColor(Color.parseColor("#E0333333"))
            setStroke(2, Color.WHITE)
        }

        val cardView = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 20)
            background = backgroundDrawable
            elevation = 20f
            
            val lp = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                topMargin = 150
                marginEnd = 50
            }
            layoutParams = lp
        }

        val title = TextView(context).apply {
            text = "A11y Issues (${issues.size})"
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            textSize = 14f
        }
        cardView.addView(title)

        if (errorCount > 0) {
            val errorText = TextView(context).apply {
                text = "• $errorCount Errors"
                setTextColor(Color.parseColor("#FF5252")) // Bright Red
                textSize = 12f
            }
            cardView.addView(errorText)
        }

        if (warningCount > 0) {
            val warningText = TextView(context).apply {
                text = "• $warningCount Warnings"
                setTextColor(Color.parseColor("#FFD740")) // Bright Orange/Amber
                textSize = 12f
            }
            cardView.addView(warningText)
        }

        addView(cardView)

        // Draggable logic
        var dX = 0f
        var dY = 0f
        cardView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dX = v.x - event.rawX
                    dY = v.y - event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    v.x = event.rawX + dX
                    v.y = event.rawY + dY
                }
            }
            true
        }
    }
}
