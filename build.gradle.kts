buildscript {
    val composeUiVersion by extra("1.2.1")
    val kotlinVersion by extra("1.7.0")
    val assessmentVersion by extra("0.10.1")
    val kermitVersion by extra("1.0.0")
    val koinVersion by extra("3.2.2")
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version ("7.3.0") apply(false)
    id("com.android.library") version("7.3.0") apply(false)
    id("org.jetbrains.kotlin.android") version("1.7.0") apply(false)
    id("maven-publish")
    id("org.jetbrains.dokka") version "1.6.21"
    kotlin("plugin.serialization") version("1.7.0")
}

tasks.dokkaHtmlMultiModule {
    outputDirectory.set(rootDir.resolve("docs"))
}

subprojects {
    afterEvaluate {
        if (project.plugins.hasPlugin("com.android.library")) {
//            val android = this.extensions.getByName("android") as com.android.build.gradle.LibraryExtension
//            val kotlin =
//                this.extensions.getByType(org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension::class.java)

            tasks.register<Jar>("javadocJar") {
                val dokkaJavadoc = tasks.getByName<org.jetbrains.dokka.gradle.DokkaTask>("dokkaJavadoc")
                dependsOn(dokkaJavadoc)
                classifier = "javadoc"
                from(dokkaJavadoc.outputDirectory)
            }
        }
    }
}

allprojects {
    group = "org.sagebionetworks.motorControl"
    version = "0.0.1"
}