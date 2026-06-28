plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.hope.accessbilitysdk"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.hope.accessbilitysdk"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

tasks.register("accessibilityCheck") {
    group = "verification"
    description = "Runs accessibility scans on the project."
    
    doLast {
        println("\n--- Accessibility CI Scanner ---")
        println("Scanning reports for violations...")
        
        // Simulating the CI failure based on the roadmap vision
        val issueCount = 25 
        if (issueCount > 0) {
            println("❌ $issueCount Issues found.")
            println("-----------------------------------")
            println("⚠ Button(id=loginButton): No content description found.")
            println("⚠ Icon: Touch target is only 32dp.")
            println("-----------------------------------")
            println("Build Failed: Accessibility regressions detected.")
            throw org.gradle.api.GradleException("Accessibility check failed.")
        }
    }
}
