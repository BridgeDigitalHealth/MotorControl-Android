buildscript {
    repositories {
        google()
        mavenCentral()
        maven {url = uri("https://plugins.gradle.org/m2/")}
    }
    dependencies {
        classpath(libs.bundles.gradlePlugins)
    }
}

plugins {
    id("org.jetbrains.dokka") version "1.9.0"
    id("maven-publish")
}

tasks.dokkaHtmlMultiModule {
    outputDirectory.set(rootDir.resolve("docs"))
}

allprojects {
    group = "org.sagebionetworks.motorcontrol"
    version = "0.0.4"
}