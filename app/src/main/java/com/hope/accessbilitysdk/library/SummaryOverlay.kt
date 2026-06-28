package com.hope.accessbilitysdk.library

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import androidx.core.graphics.toColorInt
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

class SummaryOverlay @JvmOverloads constructor(
    context: Context,
    private val issues: List<AccessibilityIssue> = emptyList(),
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        val errorCount = issues.count { it.severity == AccessibilityIssue.Severity.ERROR }
        val warningCount = issues.count { it.severity == AccessibilityIssue.Severity.WARNING }

        val backgroundDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 40f
            setColor("#E0333333".toColorInt())
            setStroke(2, Color.WHITE)
        }

        val cardView = object : LinearLayout(context) {
            override fun performClick(): Boolean {
                super.performClick()
                return true
            }
        }.apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 20)
            background = backgroundDrawable
            elevation = 20f
            
            val lp = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                topMargin = 150
                marginEnd = 50
            }
            layoutParams = lp
        }

        val titleText = "A11y Issues (${issues.size})"
        val title = TextView(context).apply {
            text = titleText
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            textSize = 14f
        }
        cardView.addView(title)

        if (errorCount > 0) {
            val errorLabel = "• $errorCount Errors"
            val errorText = TextView(context).apply {
                text = errorLabel
                setTextColor("#FF5252".toColorInt()) // Bright Red
                textSize = 12f
            }
            cardView.addView(errorText)
        }

        if (warningCount > 0) {
            val warningLabel = "• $warningCount Warnings"
            val warningText = TextView(context).apply {
                text = warningLabel
                setTextColor("#FFD740".toColorInt()) // Bright Orange/Amber
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
                    v.performClick()
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
