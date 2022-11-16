plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "org.sagebionetworks.motorcontrol"
    compileSdk = 33
    buildFeatures {
        compose = true
    }
    buildFeatures.viewBinding = true
    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0"
    }
    defaultConfig {
        minSdk = 21
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

val composeUiVersion: String by rootProject.extra
val assessmentVersion: String by rootProject.extra
val kermitVersion: String by rootProject.extra
val koinVersion: String by rootProject.extra
dependencies {
    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")

    // Google
    implementation("com.google.android.material:material:1.6.1")
    implementation("com.google.accompanist:accompanist-drawablepainter:0.16.0")

    // Android
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.activity:activity-compose:1.3.1")

    // Compose
    implementation("androidx.compose.ui:ui:$composeUiVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeUiVersion")
    implementation("androidx.compose.material:material:$composeUiVersion")

    // Sage
    implementation("org.sagebionetworks.assessmentmodel:presentation:$assessmentVersion")
    implementation("org.sagebionetworks.assessmentmodel:assessmentModel:$assessmentVersion")
    implementation("org.sagebionetworks.research.kmm:passiveData:0.4.1")

    // Kermit
    implementation("co.touchlab:kermit:$kermitVersion")
    implementation("co.touchlab:kermit-crashlytics:$kermitVersion")

    // Koin
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-android:$koinVersion")

    // JUnit and testing
    testImplementation("junit:junit:4.13.2")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeUiVersion")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeUiVersion")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeUiVersion")
}