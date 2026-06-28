# Accessibility SDK

An automated accessibility auditing SDK for Android applications. This library helps developers identify and fix accessibility issues in real-time by scanning the UI and providing visual feedback directly in the app.

## Features

- **Automated Scanning**: Automatically scans active activities for common accessibility issues.
- **Visual Overlays**: Highlights problematic views with dashed borders and warning tags.
- **Interactive Feedback**: Tap on any highlighted view to see a detailed explanation of the issue via a Snackbar.
- **Summary Dashboard**: A draggable floating card that shows the total number of errors and warnings on the current screen.
- **Jetpack Compose Support**: Scans Semantics nodes in Compose layouts.
- **JUnit Integration**: Includes a `TestRule` to fail instrumented tests if accessibility errors are found.
- **Report Exporting**: Generates JSON and HTML reports for every scan.

## Visuals

| Highlights & Tags | Summary Overlay |
| :---: | :---: |
| ![Highlights](screenshots/highlights.png) | ![Summary](screenshots/summary.png) |

## Quick Start

### 1. Initialize the SDK

Install the monitor in your `Application` class:

```kotlin
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        AccessibilityMonitor.install(this) {
            // Configuration options
            checkContrast(true)
            checkTouchTargets(true)
            checkContentDescriptions(true)
            
            // Visual feedback options
            showBorders(true)
            showTags(true)
            showSummaryOverlay(true)
            
            // Reporting
            logToLogcat(true)
            exportReports(true)
        }
    }
}
```

### 2. UI Testing Integration

Add the `AccessibilityTestRule` to your instrumented tests to ensure no regressions are introduced:

```kotlin
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val accessibilityRule = AccessibilityTestRule()

    @Test
    fun testYourUI() {
        // Your test logic...
        // After this method finishes, the scan will automatically run and fail if errors exist.
    }
}
```

## Visual Feedback

When an issue is detected, the SDK provides the following feedback:

1.  **Dashed Border**: Colors indicate severity (Red for Error, Orange for Warning, Blue for Info).
2.  **Tag (!)**: A small indicator above the view.
3.  **Floating Summary**: A draggable card showing the total issue count.
4.  **Snackbar**: Tap a highlighted view to see the "Why" and "How to fix".

## Detectors Included

| Detector | Description | Severity |
| :--- | :--- | :--- |
| **Content Description** | Clickable views must have a label or text. | ERROR |
| **Touch Target** | Clickable views should be at least 48x48dp. | WARNING |
| **Contrast Ratio** | Text must have a contrast ratio of at least 4.5:1. | WARNING |
| **Duplicate Labels** | Avoid multiple clickable views with identical labels. | WARNING |
| **Focus Order** | Detects focus traps and unreachable focusable views. | ERROR/WARNING |

## Configuration

The SDK can be customized during `install`:

- `showBorders(Boolean)`: Toggle the dashed border around views.
- `showTags(Boolean)`: Toggle the "!" indicator.
- `showSummaryOverlay(Boolean)`: Toggle the floating summary card.
- `failBuildOnErrors(Boolean)`: If true, the `AccessibilityTestRule` will throw an `AssertionError`.
- `exportFormat(ExportFormat)`: Choose between `JSON`, `HTML`, or `BOTH`.

---
*Note: This SDK is for development and testing purposes and should typically be initialized only in debug builds.*
