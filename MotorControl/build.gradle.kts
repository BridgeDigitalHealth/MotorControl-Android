plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("maven-publish")
    id("org.jetbrains.dokka")
}

android {
    namespace = "org.sagebionetworks.motorcontrol"
    compileSdk = libs.versions.compileSdk.get().toInt()
    buildFeatures {
        compose = true
    }
    buildFeatures.viewBinding = true
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()

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

        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    publishing {
        singleVariant("release") {
            // if you don't want sources/javadoc, remove these lines
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization)

    // Google
    implementation(libs.androidx.material)
    implementation(libs.google.accompanist)

    // Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.uiToolingPreview)

    // Sage
    implementation(libs.assessmentmodel.presentation)
    implementation(libs.assessmentmodel)
    implementation(libs.passiveData)

    // Kermit
    implementation(libs.touchlab.kermit)
    //implementation("co.touchlab:kermit-crashlytics:$kermitVersion")

    // Koin
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    coreLibraryDesugaring(libs.android.desugar)

    // JUnit and testing
    androidTestImplementation(composeBom)
    testImplementation(libs.junit)
    debugImplementation(libs.androidx.compose.uiTooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.uiTest)
}

//afterEvaluate {
//
//    tasks.register<Jar>("sourcesJar") {
//        from(android.sourceSets["main"].java.srcDirs)
//        classifier = "sources"
//    }
//
//    publishing {
//        publications {
//            create<MavenPublication>("motorcontrol") {
//                from(components.getByName("release"))
////                artifact(tasks.getByName("releaseSourcesJar"))
////                artifact(tasks.getByName<Jar>("javadocJar"))
//            }
//        }
//    }
//}
publishing {
    repositories {
        maven {
            url = uri("https://sagebionetworks.jfrog.io/artifactory/mobile-sdks/")
            credentials {
                username = System.getenv("artifactoryUser")
                password = System.getenv("artifactoryPwd")
            }
        }
    }
}