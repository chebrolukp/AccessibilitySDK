package com.hope.accessbilitysdk.library

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class DetectorTests {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun `ContentDescriptionDetector flags clickable view with no label`() {
        val detector = ContentDescriptionDetector()
        val view = Button(context).apply {
            isClickable = true
            contentDescription = ""
            text = ""
        }

        val issue = detector.check(view)
        assertNotNull(issue)
        assertEquals("Missing Label", issue?.title)
        assertEquals(AccessibilityIssue.Severity.ERROR, issue?.severity)
    }

    @Test
    fun `ContentDescriptionDetector ignores view with contentDescription`() {
        val detector = ContentDescriptionDetector()
        val view = Button(context).apply {
            isClickable = true
            contentDescription = "Submit"
        }

        val issue = detector.check(view)
        assertNull(issue)
    }

    @Test
    fun `ContentDescriptionDetector ignores TextView with text`() {
        val detector = ContentDescriptionDetector()
        val view = TextView(context).apply {
            isClickable = true
            text = "Hello"
        }

        val issue = detector.check(view)
        assertNull(issue)
    }

    @Test
    fun `TouchTargetDetector flags small clickable view`() {
        val detector = TouchTargetDetector()
        
        // Mocking dimensions is tricky in unit tests, so we'll rely on Robolectric's layout
        val view = Button(context).apply {
            isClickable = true
            visibility = View.VISIBLE
            // Set size to 40x40 pixels. 
            // Default density is 1.0 (160dpi) in Robolectric unless configured.
            layoutParams = android.view.ViewGroup.LayoutParams(40, 40)
        }
        
        // Force measure/layout
        view.measure(View.MeasureSpec.makeMeasureSpec(40, View.MeasureSpec.EXACTLY), 
                     View.MeasureSpec.makeMeasureSpec(40, View.MeasureSpec.EXACTLY))
        view.layout(0, 0, 40, 40)

        val issue = detector.check(view)
        assertNotNull("Issue should be detected for 40x40px view", issue)
        assertEquals("Small Touch Target", issue?.title)
    }

    @Test
    fun `TouchTargetDetector ignores large clickable view`() {
        val detector = TouchTargetDetector()
        val view = Button(context).apply {
            isClickable = true
            visibility = View.VISIBLE
            layoutParams = android.view.ViewGroup.LayoutParams(200, 200)
        }
        
        view.measure(View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.EXACTLY), 
                     View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.EXACTLY))
        view.layout(0, 0, 200, 200)

        val issue = detector.check(view)
        assertNull(issue)
    }
}
