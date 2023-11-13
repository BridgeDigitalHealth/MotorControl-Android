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

//subprojects {
//    afterEvaluate {
//        if (project.plugins.hasPlugin("com.android.library")) {
////            val android = this.extensions.getByName("android") as com.android.build.gradle.LibraryExtension
////            val kotlin =
////                this.extensions.getByType(org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension::class.java)
//
//            tasks.register<Jar>("javadocJar") {
//                val dokkaJavadoc = tasks.getByName<org.jetbrains.dokka.gradle.DokkaTask>("dokkaJavadoc")
//                dependsOn(dokkaJavadoc)
//                classifier = "javadoc"
//                from(dokkaJavadoc.outputDirectory)
//            }
//        }
//    }
//}

allprojects {
    group = "org.sagebionetworks.motorcontrol"
    version = "0.0.3"
}