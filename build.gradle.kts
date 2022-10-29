buildscript {
    val composeUiVersion by extra("1.2.1")
    val kotlinVersion by extra("1.7.0")
    val assessmentVersion by extra("0.9.1")
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version ("7.3.0") apply(false)
    id("com.android.library") version("7.3.0") apply(false)
    id("org.jetbrains.kotlin.android") version("1.7.0") apply(false)
    kotlin("plugin.serialization") version("1.7.0")
}