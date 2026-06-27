package com.hope.accessbilitysdk.library

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class HighlightOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val issues: List<AccessibilityIssue> = emptyList()
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 8f
        pathEffect = DashPathEffect(floatArrayOf(20f, 10f), 0f)
    }

    private val labelPaint = Paint().apply {
        color = Color.WHITE
        textSize = 32f
        isFakeBoldText = true
    }

    private val labelBackgroundPaint = Paint().apply {
        style = Paint.Style.FILL
    }

    private val tempLocation = IntArray(2)
    private val tempOverlayLocation = IntArray(2)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        issues.forEach { issue ->
            val view = issue.view
            this.getLocationOnScreen(tempOverlayLocation)

            val left: Float
            val top: Float
            val right: Float
            val bottom: Float

            if (issue.customBounds != null) {
                left = (issue.customBounds.left - tempOverlayLocation[0]).toFloat()
                top = (issue.customBounds.top - tempOverlayLocation[1]).toFloat()
                right = (issue.customBounds.right - tempOverlayLocation[0]).toFloat()
                bottom = (issue.customBounds.bottom - tempOverlayLocation[1]).toFloat()
            } else {
                view.getLocationOnScreen(tempLocation)
                left = (tempLocation[0] - tempOverlayLocation[0]).toFloat()
                top = (tempLocation[1] - tempOverlayLocation[1]).toFloat()
                right = left + view.width
                bottom = top + view.height
            }

            // Set color based on severity
            val color = when (issue.severity) {
                AccessibilityIssue.Severity.ERROR -> Color.RED
                AccessibilityIssue.Severity.WARNING -> Color.rgb(255, 165, 0) // Orange
                AccessibilityIssue.Severity.INFO -> Color.BLUE
            }
            paint.color = color
            labelBackgroundPaint.color = color

            // Draw border
            canvas.drawRect(left, top, right, bottom, paint)

            // Draw small tag
            val tagText = "!"
            canvas.drawRect(left, top - 40, left + 40, top, labelBackgroundPaint)
            canvas.drawText(tagText, left + 12, top - 10, labelPaint)
        }
    }
}
